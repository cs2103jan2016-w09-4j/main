//@@author Gilbert
package logic;

import common.*;
import common.Command.CommandType;
import storage.Storage;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

public class Execution {
    
    private Storage storage;
    
    private static ArrayList<Task> mainList;
    private static ArrayList<Task> doneList;
    private static ArrayList<Category> categories;
    private static ArrayList<Task> searchResults;
    private static ArrayList<Task> previousCopyOfMainList;
    private static ArrayList<Task> copyOfMainListForRedo;
    
    // Store user input history for auto-completion
    // TreeSet is used to avoid duplicate entries
    // Integer is the frequency that the String is entered by the user
    private static TreeSet<Entry<String, Integer>> taskDictionary;
    private static TreeSet<Entry<String, Integer>> wordDictionary;
    private static TreeSet<String> fileDictionary;
    private LocalDateTime current;
    
    // Common English function words 
    private static final String[] functionWords = { "a", "about", "an", "and", "as", "at",
                                                  "by", "for", "in", "of", "or",
                                                  "the", "to", "with" };

    // Comparator for dictionaries, where element uniqueness depends only on the String
    // and not the frequency count
    private static final Comparator<Entry<String, Integer>> dictionaryComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            // element uniqueness depends only on the entry's key
            return entry1.getKey().compareTo(entry2.getKey());
        }
    };
    
    public Execution() {
        storage = new Storage();
        mainList = storage.getMainList();
        doneList = storage.getCompletedList();
        categories = new ArrayList<Category>();
        categories.add(new Category("Priority"));
        categories.add(new Category("Today"));
        searchResults = new ArrayList<Task>();
        previousCopyOfMainList = new ArrayList<Task>();
        copyOfMainListForRedo = new ArrayList<Task>();

        taskDictionary = new TreeSet<Entry<String, Integer>>(dictionaryComparator);
        // TODO: update type to Entry<String, Integer> 
        wordDictionary = new TreeSet<Entry<String, Integer>>(dictionaryComparator);
        fileDictionary = new TreeSet<String>();
    }
    
    /********************************
     * METHODS FOR COMAND EXECUTION *
     ********************************/
    
    public Result addTask(String description, LocalDateTime start, LocalDateTime end) {
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        if(description.isEmpty()){
        	sortList(mainList);
        	return new Result(CommandType.ADD, false, "No whitespace!", mainList);
        }
        
        // create a new Task with specified details
        Task newTask = new Task(description);
        if (start != null) {
            newTask.setStart(start);
        }
        if (end != null) {
            newTask.setEnd(end);
        }
        newTask.setModified(true);
        
        // add to list
        mainList.add(newTask);

        // postprocessing
        sortList(mainList);
        updateDictionary(description);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        taskProgression();
        return new Result(CommandType.ADD, true, "Added task", mainList);
    }

    public Result completeCommand(int taskID){
        clearModifiedStatus();
        saveMainListForUndo();
        
        int index = taskID - 1;
        Task doneTask = mainList.get(index);
        doneList.add(doneTask);
        deleteTask(taskID);
        
        taskProgression();
        return new Result(CommandType.DONE, true, "Marked as completed", mainList);
    }
    
    public Result deleteTask(int taskID) {
        clearModifiedStatus();
        saveMainListForUndo();
        
        // remove from list
        int index = taskID - 1;
        try {
            mainList.remove(index);
        } catch (IndexOutOfBoundsException e) {
            return new Result(CommandType.DELETE, false, "Wrong task number", mainList);
        }
        
        sortList(mainList);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskProgression();
        return new Result(CommandType.DELETE, true, "Deleted", mainList);    
    }
    
    public Result editTask(int taskID, String newDescription, LocalDateTime start, LocalDateTime end) {
        clearModifiedStatus();
        saveMainListForUndo();
        
        if(newDescription.isEmpty()){
        	sortList(mainList);
        	return new Result(CommandType.ADD, false, "No whitespace!", mainList);
        }
        
        // edit task with the specified details
        int index = taskID - 1;
       
        try {
            mainList.remove(index);
        } catch (IndexOutOfBoundsException e) {
            return new Result(CommandType.EDIT, false, "Wrong task number", mainList);
        }
        
        Task task = new Task(newDescription);
        
        if (start != null) {
            task.setStart(start);
        }
        if (end != null) {
            task.setEnd(end);
        }
        task.setModified(true);

        mainList.add(task);
        sortList(mainList);
        updateDictionary(newDescription);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        taskProgression();
        return new Result(CommandType.EDIT, true, "Edited", mainList);
    }

    public Result searchTask(String keyword) {
        clearModifiedStatus();
        searchResults.clear();
        updateDictionary(keyword);
        
        for (int i = 0; i < mainList.size(); i++) {
            String descriptionLowerCase = mainList.get(i).getDescription().toLowerCase();
            String keywordLowerCase = keyword.toLowerCase();
            if (descriptionLowerCase.contains(keywordLowerCase)) {
                searchResults.add(mainList.get(i));
            }
        }
        
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }
    
    public Result savingTasks(String description){
    	sortList(mainList);
    	storage.setMainList(mainList);
    	
    	try{
            if(description.contains(" ")){
                String[] split = description.split(" ");
                String directory = split[0].toLowerCase();
                String userFileName = split[1];
                storage.saveToFileWithDirectory(directory, userFileName);
                updateFileDictionary(directory);
                updateFileDictionary(userFileName);
            } else{
                storage.saveToFile(description);
                updateFileDictionary(description);
            }  
            return new Result(CommandType.SAVE, true, "Saved at " + description, mainList);
            
        } catch (Exception e){
        	return new Result(CommandType.SAVE, false, "Failed to save " + description, mainList);
        }
    }
    
    public Result loadingTasks(String description){
    	
        description = description.trim();
        clearModifiedStatus();
        // store temp copy of original list in case loading of new list fails
        ArrayList<Task> temp = new ArrayList<Task>();
        temp.addAll(mainList);
        
        try {
            ArrayList<Task> loadBack = new ArrayList<Task>();
            if(description.contains(" ")){
                String[] split = description.split(" ");
                String directory = split[0].toLowerCase();
                String userFileName = split[1];
                loadBack = storage.loadFileWithDirectory(directory, userFileName);
                setMainList(loadBack);
                sortList(mainList);
                updateFileDictionary(directory);
                updateFileDictionary(userFileName);
                return new Result(CommandType.LOAD, true, "Loaded " + description, mainList);
            } else{
                loadBack = storage.loadFileWithFileName(description);
                setMainList(loadBack);
                sortList(mainList);
                updateFileDictionary(description);
                return new Result(CommandType.LOAD, true, "Loaded " + description, mainList);
            }   
        }  catch (IOException | ParseException e) {
            return new Result(CommandType.LOAD, false, "Failed to load " + description, temp);
        }
    }

    public ArrayList<Task> undoCommand() {
        // transfer content from previousCopyOfMainList to mainList
        copyOfMainListForRedo.clear();
        copyOfMainListForRedo.addAll(mainList);
        mainList.clear();
        mainList.addAll(previousCopyOfMainList);
        
     // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sortList(previousCopyOfMainList);
        return previousCopyOfMainList;
    }
    
    public ArrayList<Task> redoCommand() {
        mainList.clear();
        mainList.addAll(copyOfMainListForRedo);
        
     // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sortList(mainList);
        return mainList;
    }
    
    public void taskProgression(){
    	current = LocalDateTime.now();
    	
    	sortList(mainList);
    	int count = 0;
    	LocalDate currentDate = current.toLocalDate();  
    	try{
    		for(Task task : mainList){
    			LocalDateTime taskDeadline = task.getEndDate();
    			if(taskDeadline != null){
    					LocalDate taskDeadlineDate = taskDeadline.toLocalDate();
    					if (currentDate.equals(taskDeadlineDate)){
    						count++;
    						// modify the 'today' category
    						categories.get(1).setCount(count);
    					}
    			}
    		}	
    	} catch(NullPointerException e){
    	
    	}
    	
    	if(mainList.size() == 0){
    		categories.get(1).setCount(0);
    	}
    }
    
    /******************
     * HELPER METHODS *
     ******************/
    
    public void setMainList(ArrayList<Task> mainList){
        Execution.mainList = mainList;
        sortList(mainList);
    }
    
    public void saveMainListForUndo() {
        previousCopyOfMainList.clear();
        previousCopyOfMainList.addAll(mainList);
    }

    // Sorts the list of tasks and updates task id
    private void sortList(ArrayList<Task> thisList) {
        Collections.sort(thisList);
        int id = 1;
        for (Task task : thisList) {
            task.setId(id);
            id++;
        }
    }
    
    // Clears the status of all tasks to be unmodified
    private void clearModifiedStatus() {
        for (Task task : mainList) {
            task.setModified(false);
        }
    }
    
    private void updateDictionary(String text) {
        text = text.toLowerCase();
        updateTaskDictionary(text);
        updateWordDictionary(text);
    }

    //@@author Ruoling
    private void updateTaskDictionary(String text) {
        int freqCount = removeFromDictionary(taskDictionary, text);
        addToDictionary(taskDictionary, text, ++freqCount);
    }

    private void updateWordDictionary(String text) {
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!isNumberOrFunctionWord(words[i])) {
                int freqCount = removeFromDictionary(wordDictionary, words[i]);
                addToDictionary(wordDictionary, words[i], ++freqCount);
            }
        }
    }
    
    // Remove the text from the dictionary if it exists and return its frequency count
    // If it does not already exist in the dictionary, frequency count is 0
    private int removeFromDictionary(TreeSet<Entry<String, Integer>> dictionary, String text) {
        int freqCount = 0;
        Iterator<Entry<String, Integer>> iterator = dictionary.iterator();
        while (iterator.hasNext()) {
            Entry<String, Integer> next = iterator.next();
            if (next.getKey().equals(text)) {
                // Keep the frequency count and remove the entry
                freqCount = next.getValue();
                iterator.remove();
                break;
            }
        }
        return freqCount;
    }

    // Add the text to the dictionary with its frequency count
    private void addToDictionary(TreeSet<Entry<String, Integer>> dictionary, String text, int freqCount) {
        dictionary.add(new AbstractMap.SimpleEntry<String, Integer>(text, freqCount));
    }

    private void updateFileDictionary(String text) {
        fileDictionary.add(text);
    }
        
    // Returns true if the specified word is a number or function word
    private boolean isNumberOrFunctionWord(String word) {
        // Check if word contains only numbers and the decimal separator
        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return true;
        }
        // Check if word is a pre-defined function word
        // index will be >= 0 if it is
        int index = Arrays.binarySearch(functionWords, word, String.CASE_INSENSITIVE_ORDER);
        return index >= 0 ? true : false;
    }

    //@@author Gilbert
    /******************
     * GETTER METHODS *
     ******************/
    
    public ArrayList<Task> getMainList() {
        return mainList;
    }

    public ArrayList<Task> getDoneList(){
        return doneList;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }
        
    public ArrayList<Task> getPreviousList(){
        return previousCopyOfMainList;
    }

    public TreeSet<Entry<String, Integer>> getTaskDictionary(){
        return taskDictionary;
    }

    public TreeSet<Entry<String, Integer>> getWordDictionary(){
        return wordDictionary;
    }
    
    public TreeSet<String> getFileDictionary(){
        return fileDictionary;
    }

}
