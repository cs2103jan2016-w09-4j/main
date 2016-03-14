package logic;

import common.*;
import storage.Storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Collections;

public class Execution{
	
	private Storage storage;
	
	private static ArrayList<Task> mainList;
	private static ArrayList<Task> searchResults;
	private static ArrayList<Task> doneList;
	private static ArrayList<Task> previousCopyOfMainList;
	private static ArrayList<Task> copyOfMainListForRedo;
	private ArrayList<String> dictionary;
	
	
	public Execution(){
		
		storage = new Storage();
		mainList = new ArrayList<Task>();
		doneList = new ArrayList<Task>();
		searchResults = new ArrayList<Task>();
		previousCopyOfMainList = new ArrayList<Task>();
		copyOfMainListForRedo = new ArrayList<Task>();
		dictionary = new ArrayList<String>();
	
	}
	
	public ArrayList<Task> addTask(String description) {
		Task newTask = new Task(description);
		
		dictionary.add(description);
		
		if (!mainList.isEmpty()) {
			saveMainListForUndo();
		}
		
		mainList.add(newTask);
		storage.setMainList(mainList);
		try {
			storage.appendToFile(newTask);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mainList;
	}
	
	public ArrayList<Task> completeCommand(int taskID){
		Task doneTask = mainList.get(taskID);
		doneList.add(doneTask);
		
		deleteTask(taskID);
		return doneList;
	}
	
	public ArrayList<Task> deleteTask(int taskID) {
		boolean foundTask = false;
		int index = taskID - 1;
		
		if(mainList.get(index) != null){
			mainList.remove(index);
			foundTask = true;
		}
		
		if (!foundTask) {
			return new ArrayList<Task>();
		} else {
			try {
				storage.writeToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		storage.setMainList(mainList);
		return mainList;	
	}
	
	public ArrayList<Task> editTask(int taskID, String newDescription) {
		boolean foundTask = false;
		dictionary.add(newDescription);
		
		if(mainList.get(taskID) != null){
			mainList.get(taskID).setDescription(newDescription);
			foundTask = true;
		}
		if (!foundTask) {
			return new ArrayList<Task>();
		} else {
			try {
				storage.writeToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		storage.setMainList(mainList);
		return mainList;
	}
	
	public ArrayList<Task> searchTask(String keyword) {
		searchResults.clear();
		dictionary.add(keyword);
		for (int i = 0; i < mainList.size(); i++) {
			if (mainList.get(i).getDescription().contains(keyword)) {
				searchResults.add(mainList.get(i));
			}
		}
		return searchResults;
	}
	
	public void savingTasks(String description){
		try{	
			if(description.contains(" ")){
				String[] split = description.split(" ");
				String directory = split[0].toLowerCase();
				String userFileName = split[1];
				storage.saveToFileWithDirectory(directory, userFileName);
				dictionary.add(directory);
				dictionary.add(userFileName);
			} else{
				storage.saveToFile(description);
				dictionary.add(description);
			}	
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> loadingTasks(String description){
		try {
			ArrayList<Task> loadBack = new ArrayList<Task>();
			if(description.contains(" ")){
				String[] split = description.split(" ");
				String directory = split[0].toLowerCase();
				String userFileName = split[1];
				loadBack = storage.loadFileWithDirectory(directory, userFileName);
				setMainList(loadBack);
				dictionary.add(directory);
				dictionary.add(userFileName);
				return loadBack;
			} else{					
				loadBack = storage.loadFileWithFileName(description);
				setMainList(loadBack);
				dictionary.add(description);
				return loadBack;
			}	
		} catch (NotDirectoryException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<Task>();
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
	}
		
	// edition
	public ArrayList<Task> getPreviousList(){
		return previousCopyOfMainList;
	}
	
	public ArrayList<String> getDictionary(){
		Collections.sort(dictionary);
		return dictionary;	
	}
	
	public ArrayList<Task> getDoneList(){
		return doneList;
	}
	
}