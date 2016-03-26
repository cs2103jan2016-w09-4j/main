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
    }
    
    public Result addTask(String description, LocalDateTime start, LocalDateTime end) {
        clearModifiedStatus();
        Task newTask = new Task(description);
        if (start != null) {
            newTask.setStart(start);
        }
        if (end != null) {
            newTask.setEnd(end);
        }
        newTask.setModified(true);

        updateDictionary(description);
        
        if (!mainList.isEmpty()) {
            saveMainListForUndo();
        }
        
        mainList.add(newTask);
        storage.setMainList(mainList);
        try {
        	storage.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sortList();
        return new Result(CommandType.ADD, true, "Added task", mainList);
    }

    public Result completeCommand(int taskID){
        clearModifiedStatus();
        int index = taskID - 1;
        Task doneTask = mainList.get(index);
        doneList.add(doneTask);
        
        deleteTask(taskID);
        return new Result(CommandType.DONE, true, "Marked as completed", mainList);
    }
    
    public Result deleteTask(int taskID) {
        clearModifiedStatus();
        
        boolean foundTask = false;
        int index = taskID - 1;
        
        if(mainList.get(index) != null){
            mainList.remove(index);
            foundTask = true;
        }
        
        if (!foundTask) {
            return new Result(CommandType.DELETE, false, "Wrong task number", mainList);
        } else {
            try {
                storage.writeToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        storage.setMainList(mainList);
        sortList();
        return new Result(CommandType.DELETE, true, "Deleted", mainList);    
    }
    
    public Result editTask(int taskID, String newDescription, LocalDateTime start, LocalDateTime end) {
        clearModifiedStatus();
        
        boolean foundTask = false;
        updateDictionary(newDescription);
        int index = taskID - 1;
        Task task = mainList.get(index);
        
        if(task != null){
            task.setDescription(newDescription);
            if (start != null) {
                task.setStart(start);
            }
            if (end != null) {
                task.setEnd(end);
            }
            task.setModified(true);
            foundTask = true;
        }
        if (!foundTask) {
            return new Result(CommandType.EDIT, false, "Wrong task number", mainList);
        } else {
            try {
                storage.writeToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        sortList();
        storage.setMainList(mainList);
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
        clearModifiedStatus();
        description = description.trim();
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
        dictionary.add(text);
    }
    
    private void updateFileDictionary(String text) {
        fileDictionary.add(text);
    }
    
    public void saveMainListForUndo() {
        previousCopyOfMainList.clear();
        for (int i=0; i< mainList.size(); i++) {
            previousCopyOfMainList.add(mainList.get(i));
        }
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
    
    private void clearModifiedStatus() {
        for (Task task : mainList) {
            task.setModified(false);
        }
    }
    
}
