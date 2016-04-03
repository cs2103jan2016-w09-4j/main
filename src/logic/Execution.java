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
    private static ArrayList<Task> searchResults;
    private static ArrayList<Task> previousCopyOfMainList;
    private static ArrayList<Task> copyOfMainListForRedo;
    private boolean canUndo = false;
    private boolean canRedo = false;
    
    // Store user input history for auto-completion
    // TreeSet is used to avoid duplicate entries and for faster lookup
    // Integer is the frequency that the String is entered by the user
    private static TreeSet<Entry<String, Integer>> taskDictionary;
    private static TreeSet<Entry<String, Integer>> wordDictionary;
    private static TreeSet<Entry<String, Integer>> fileDictionary;

    // Store information about categories
    // Integer is the number of tasks belonging to a category
    private static TreeSet<Entry<String, Integer>> categories;
    
    private static final String CATEGORY_PRIORITY = "Priority";
    private static final String CATEGORY_TODAY = "Today";

    // Common English function words, will not be stored in word dictionary
    private static final String[] functionWords = { "a", "about", "an", "and", "as", "at",
                                                  "by", "for", "in", "of", "or",
                                                  "the", "to", "with" };

    // Comparator where element uniqueness depends only on the
    // String key and not the frequency count
    private static final Comparator<Entry<String, Integer>> keyComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            // element uniqueness depends only on the entry's key
            return entry1.getKey().compareToIgnoreCase(entry2.getKey());
        }
    };
    
    public Execution() {
        storage = new Storage();
        mainList = storage.getMainList();
        doneList = storage.getCompletedList();
        searchResults = new ArrayList<Task>();
        previousCopyOfMainList = new ArrayList<Task>();
        copyOfMainListForRedo = new ArrayList<Task>();     
        
        categories = new TreeSet<Entry<String, Integer>>(keyComparator);
        updateTaskProgress();

        taskDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
        wordDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
        fileDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
    }
    
    /*********************************
     * METHODS FOR COMMAND EXECUTION *
     *********************************/
    
    public Result addTask(String description, LocalDateTime start, LocalDateTime end, ArrayList<String> categories) {
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        // validate user input
        if(description.isEmpty()){
        	sortList(mainList);
        	return new Result(CommandType.ADD, false, "No description!", mainList);
        }
        
        // create a new Task with specified details
        Task newTask = new Task(description);
        if (start != null) {
            newTask.setStart(start);
        }
        if (end != null) {
            newTask.setEnd(end);
        }
        if (categories != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(CATEGORY_PRIORITY)) {
                    newTask.setImportance(true);
                } else {
                    list.add(toSentenceCase(cat));
                }
            }
            newTask.setCategories(list);
        }
        newTask.setModified(true);
        
        // add to list
        mainList.add(newTask);

        // postprocessing
        sortList(mainList);
        updateDictionary(description);
        canUndo = true;
        canRedo = false;
        updateTaskProgress();

        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            return new Result(CommandType.ADD, false, "Couldn't save", mainList);
        }
        
        return new Result(CommandType.ADD, true, "Added task", mainList);
    }

    public Result completeCommand(int taskID){
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();

        // validate user input
        int index = taskID - 1;
        try {
            Task doneTask = mainList.remove(index);
            doneList.add(doneTask);
            
            // postprocessing
            sortList(mainList);
            canUndo = true;
            canRedo = false;
            updateTaskProgress();

            // save
            storage.setMainList(mainList);
            storage.setCompletedList(doneList);
            storage.writeToFile();
        } catch (IndexOutOfBoundsException ie) {
            canUndo = false;
            canRedo = false;
            return new Result(CommandType.DONE, false, "Wrong task number", mainList);
        } catch (IOException ioe) {
            return new Result(CommandType.DONE, false, "Couldn't save", mainList);
        }
        
        return new Result(CommandType.DONE, true, "Marked as completed", mainList);
    }
    
    public Result deleteTask(int taskID) {
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        // remove from list
        int index = taskID - 1;
        try {
            mainList.remove(index);
        } catch (IndexOutOfBoundsException e) {
            return new Result(CommandType.DELETE, false, "Wrong task number", mainList);
        }
        
        // post-processing
        sortList(mainList);
        canUndo = true;
        canRedo = false;
        updateTaskProgress();
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            return new Result(CommandType.DELETE, false, "Couldn't save", mainList);
        }
        
        return new Result(CommandType.DELETE, true, "Deleted", mainList);    
    }
    
    public Result editTask(int taskID, String newDescription, LocalDateTime start, LocalDateTime end, ArrayList<String> categories) {
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();

        // validate user input
        if(newDescription.isEmpty()){
        	sortList(mainList);
        	return new Result(CommandType.ADD, false, "No description!", mainList);
        }
        
        // remove task with specified ID
        int index = taskID - 1;
        try {
            mainList.remove(index);
        } catch (IndexOutOfBoundsException e) {
            canUndo = false;
            canRedo = false;
            return new Result(CommandType.EDIT, false, "Wrong task number", mainList);
        }
        
        // create task with specified details 
        Task task = new Task(newDescription);
        if (start != null) {
            task.setStart(start);
        }
        if (end != null) {
            task.setEnd(end);
        }
        if (categories != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (String cat : categories) {
                if (cat.equalsIgnoreCase(CATEGORY_PRIORITY)) {
                    task.setImportance(true);
                } else {
                    list.add(toSentenceCase(cat));
                }
            }
            task.setCategories(list);
        }
        task.setModified(true);

        // add to list
        mainList.add(task);
        
        // post-processing
        sortList(mainList);
        canUndo = true;
        canRedo = false;
        updateDictionary(newDescription);
        updateTaskProgress();
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            return new Result(CommandType.EDIT, false, "Couldn't save", mainList);
        }
        
        return new Result(CommandType.EDIT, true, "Edited", mainList);
    }

    public Result searchTask(String keyword) {
        // pre-processing
        clearModifiedStatus();
        searchResults.clear();
        
        for (int i = 0; i < mainList.size(); i++) {
            String descriptionLowerCase = mainList.get(i).getDescription().toLowerCase();
            String keywordLowerCase = keyword.toLowerCase();
            if (descriptionLowerCase.contains(keywordLowerCase)) {
                searchResults.add(mainList.get(i));
            }
        }

        // post-processing
        updateDictionary(keyword);
        canUndo = false;
        canRedo = false;
        
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }
    
    public Result savingTasks(String description) {
    	sortList(mainList);
    	storage.setMainList(mainList);
    	storage.setCompletedList(doneList);
    	
    	try {
            if (description.contains(" ")) {
                String[] split = description.split(" ");
                String directory = split[0].toLowerCase();
                String userFileName = split[1];
                storage.saveToFileWithDirectory(directory, userFileName);
                updateFileDictionary(directory);
                updateFileDictionary(userFileName);
            } else {
                storage.saveToFile(description);
                updateFileDictionary(description);
            }
            
            /// post-processing
            canUndo = false;
            canRedo = false;
            
            return new Result(CommandType.SAVE, true, "Saved at " + description, mainList);
        } catch (Exception e){
            canUndo = false;
            canRedo = false;
            
        	return new Result(CommandType.SAVE, false, "Couldn't save data at " + description, mainList);
        }
    }
    
    public Result loadingTasks(String description) {
        description = description.trim();
        clearModifiedStatus();
        // store temp copy of original list in case loading of new list fails
        ArrayList<Task> tempMainList = new ArrayList<Task>();
        tempMainList.addAll(mainList);
        ArrayList<Task> tempDoneList = new ArrayList<Task>();
        tempDoneList.addAll(doneList);
        
        try {
            ArrayList<Task> loadBack = new ArrayList<Task>();
            if (description.contains(" ")) {
                String[] split = description.split(" ");
                String directory = split[0].toLowerCase();
                String userFileName = split[1];
                loadBack = storage.loadFileWithDirectory(directory, userFileName);
                setMainList(loadBack);
                doneList = storage.getCompletedList();
                updateFileDictionary(directory);
                updateFileDictionary(userFileName);
            } else {
                loadBack = storage.loadFileWithFileName(description);
                setMainList(loadBack);
                updateFileDictionary(description);
            }   
            
            /// post-processing
            canUndo = false;
            canRedo = false;
            
            return new Result(CommandType.LOAD, true, "Loaded " + description, mainList);
        }  catch (IOException | ParseException e) {
            mainList = tempMainList;
            doneList = tempDoneList;
            canUndo = false;
            canRedo = false;
            
            return new Result(CommandType.LOAD, false, "Failed to load " + description, mainList);
        }
    }

    public Result undoCommand() {
        if (!canUndo) {
            canRedo = false;
            return new Result(CommandType.UNDO, false, "Cannot undo previous command", mainList);
        }
        
        // transfer content from previousCopyOfMainList to mainList
        copyOfMainListForRedo.clear();
        copyOfMainListForRedo.addAll(mainList);
        mainList.clear();
        mainList.addAll(previousCopyOfMainList);

        // post-processing
        canUndo = false;
        canRedo = true;
        sortList(previousCopyOfMainList);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            return new Result(CommandType.UNDO, false, "Couldn't save", previousCopyOfMainList);
        }
        
        return new Result(CommandType.UNDO, true, "Last command undone", previousCopyOfMainList);
    }
    
    public Result redoCommand() {
    	if(!canRedo){
            return new Result(CommandType.REDO, false, "Previous command was not undo", mainList);
    	}
    	
		mainList.clear();
		mainList.addAll(copyOfMainListForRedo);
	    
        // post-processing
        sortList(mainList);
        canUndo = false;
        canRedo = false;
        
		// save
		storage.setMainList(mainList);
		try {
			storage.writeToFile();
		} catch (IOException e) {
            return new Result(CommandType.REDO, false, "Couldn't save", mainList);
		}

		return new Result(CommandType.REDO, true, "Redone", mainList);
    }
    
    public void updateTaskProgress() {
        categories.clear();
        categories.add(new AbstractMap.SimpleEntry<String, Integer>(CATEGORY_PRIORITY, 0));
        categories.add(new AbstractMap.SimpleEntry<String, Integer>(CATEGORY_TODAY, 0));
        LocalDate currentDate = LocalDate.now();

    	for (Task task : mainList) {
    	    if (task.isImportant()) {
    	        // update 'Priority' category count
    	        int count = removeFromDictionary(categories, CATEGORY_PRIORITY);
                addToDictionary(categories, CATEGORY_PRIORITY, ++count);
    	    }

    	    if (task.isOccurringOn(currentDate)) {
    	        // update 'Today' category count
    	        int count = removeFromDictionary(categories, CATEGORY_TODAY);
    	        addToDictionary(categories, CATEGORY_TODAY, ++count);
    	    }
    	    
    	    // Other categories
    	    for (String cat : task.getCategories()) {
                int count = removeFromDictionary(categories, cat);
                addToDictionary(categories, cat, ++count);
    	    }
    	    
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

    private String toSentenceCase(String text) {
        String sentenceCase = text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();
        return sentenceCase;
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
            if (next.getKey().equalsIgnoreCase(text)) {
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
        int freqCount = removeFromDictionary(fileDictionary, text);
        addToDictionary(fileDictionary, text, ++freqCount);
    }
        
    // Returns true if the specified word is a number or function word
    private boolean isNumberOrFunctionWord(String word) {
        // Check if word contains only numbers and the decimal separator
        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return true;
        }
        // Check if word is a pre-defined function word
        // index will be >= 0 if it is
        String wordLowerCase = word.toLowerCase();
        int index = Arrays.binarySearch(functionWords, wordLowerCase, String.CASE_INSENSITIVE_ORDER);
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

    public TreeSet<Entry<String, Integer>> getCategories() {
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
    
    public TreeSet<Entry<String, Integer>> getFileDictionary(){
        return fileDictionary;
    }

}
