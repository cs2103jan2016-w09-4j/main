package logic;

import common.*;
import common.Command.CommandType;
import storage.Storage;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class Execution {
    
    private Storage storage;
    
    private static ArrayList<Task> mainList;
    private static ArrayList<Task> searchResults;
    private static ArrayList<Task> doneList;
    private static ArrayList<Task> previousCopyOfMainList;
    private static ArrayList<Task> copyOfMainListForRedo;
    private static ArrayList<Category> categories;
    private TreeSet<String> dictionary;
    private TreeSet<String> fileDictionary;
    private TreeSet<String> wordDictionary;
    
    public static final String[] specialWords = { "a", "an", "and", "at", "of", "or", "the", "to" }; 
    
    public Execution() {
        storage = new Storage();
        mainList = storage.getMainList();
        doneList = new ArrayList<Task>();
        searchResults = new ArrayList<Task>();
        previousCopyOfMainList = new ArrayList<Task>();
        copyOfMainListForRedo = new ArrayList<Task>();
        categories = new ArrayList<Category>();
        categories.add(new Category("Priority"));
        categories.add(new Category("Today"));
        dictionary = new TreeSet<String>();
        fileDictionary = new TreeSet<String>();
        wordDictionary = new TreeSet<String>();
    }
    
    public Result addTask(String description, LocalDateTime start, LocalDateTime end) {
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
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
        sortList();
        updateDictionary(description);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new Result(CommandType.ADD, true, "Added task", mainList);
    }

    public Result completeCommand(int taskID){
        clearModifiedStatus();
        saveMainListForUndo();
        
        int index = taskID - 1;
        Task doneTask = mainList.get(index);
        doneList.add(doneTask);
        deleteTask(taskID);
        
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

        sortList();
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new Result(CommandType.DELETE, true, "Deleted", mainList);    
    }
    
    public Result editTask(int taskID, String newDescription, LocalDateTime start, LocalDateTime end) {
        clearModifiedStatus();
        saveMainListForUndo();
        
        // edit task with the specified details
        int index = taskID - 1;
        Task task = null;
        try {
            task = mainList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return new Result(CommandType.EDIT, false, "Wrong task number", mainList);
        }
        if (task == null) { // will task ever be null?
            return new Result(CommandType.EDIT, false, "Couldn't edit that task", mainList);
        }
        task.setDescription(newDescription);
        if (start != null) {
            task.setStart(start);
        }
        if (end != null) {
            task.setEnd(end);
        }
        task.setModified(true);

        sortList();
        updateDictionary(newDescription);
        
        // save
        storage.setMainList(mainList);
        try {
            storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return new Result(CommandType.EDIT, true, "Edited", mainList);
    }

    public Result searchTask(String keyword) {
        clearModifiedStatus();
        searchResults.clear();
        updateDictionary(keyword);
        
        for (int i = 0; i < mainList.size(); i++) {
            if (mainList.get(i).getDescription().contains(keyword)) {
                searchResults.add(mainList.get(i));
            }
        }
        
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }
    
    public void savingTasks(String description){
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
        } catch (Exception e){
            e.printStackTrace();
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
                sortList();
                updateFileDictionary(directory);
                updateFileDictionary(userFileName);
                return new Result(CommandType.LOAD, true, "Loaded " + description, mainList);
            } else{
                loadBack = storage.loadFileWithFileName(description);
                setMainList(loadBack);
                sortList();
                updateFileDictionary(description);
                return new Result(CommandType.LOAD, true, "Loaded " + description, mainList);
            }   
        }  catch (IOException | ParseException e) {
			e.printStackTrace();
            return new Result(CommandType.LOAD, false, "Failed to load " + description, temp);
        }
    }
    
    private void updateDictionary(String text) {
        dictionary.add(text.toLowerCase());
        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!isSpecialWord(words[i])) {
                wordDictionary.add(words[i].toLowerCase());
            }
        }
    }
    
    private boolean isSpecialWord(String word) {
        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return true;
        }
        for (int i = 0; i < specialWords.length; i++) {
            if (word.equalsIgnoreCase(specialWords[i])) {
                return true;
            }
        }
        return false;
    }

    private void updateFileDictionary(String text) {
        fileDictionary.add(text);
    }
    
    public void saveMainListForUndo() {
        previousCopyOfMainList.clear();
        previousCopyOfMainList.addAll(mainList);
    }

    public ArrayList<Task> undoCommand() {
        // transfer content from previousCopyOfMainList to mainList
        copyOfMainListForRedo.clear();
        copyOfMainListForRedo.addAll(mainList);
        mainList.clear();
        mainList.addAll(previousCopyOfMainList);
        return previousCopyOfMainList;
    }
    
    public ArrayList<Task> redoCommand() {
        mainList.clear();
        mainList.addAll(copyOfMainListForRedo);
        return mainList;
    }
    
    public ArrayList<Task> getMainList() {
        return mainList;
    }
    
    public void setMainList(ArrayList<Task> mainList){
        Execution.mainList = mainList;
        sortList();
    }
        
    // edition
    public ArrayList<Task> getPreviousList(){
        return previousCopyOfMainList;
    }
    
    public TreeSet<String> getDictionary(){
        return dictionary;
    }
    
    public TreeSet<String> getWordDictionary(){
        return wordDictionary;
    }
            
    public ArrayList<Task> getDoneList(){
        return doneList;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }
    
    // sort and update task id
    private void sortList() {
        Collections.sort(mainList);
        for (int i = 0; i < mainList.size(); i++) {
            mainList.get(i).setId(i + 1);
        }
    }
    
    // set all tasks status to be unmodified
    private void clearModifiedStatus() {
        for (Task task : mainList) {
            task.setModified(false);
        }
    }
    
}
