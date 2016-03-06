package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import common.Task;

public class Storage {

	private static ArrayList<Task> mainList;
	private static ArrayList<Task> searchResults;
	private static String fileName;

	private static final String MESSAGE_IOEXCEPTION_ERROR = "IO Exception error";
	private static final String MESSAGE_INVALID_DIRECTORY = "Invalid directory";
	
	// default file name is "mytextfile.txt"
	public Storage() {
		mainList = new ArrayList<Task>();
		searchResults = new ArrayList<Task>();
		fileName = "mytextfile.txt";
	}

	/*public Storage(String newFileName) {
		mainList = new ArrayList<Task>();
		searchResults = new ArrayList<Task>();
		fileName = newFileName;
	}*/

	//rewrite whole file
	private void writeToFile() {
		try {
			FileWriter writer = new FileWriter(fileName);
			for (int i=0; i<mainList.size(); i++) {
				String toWrite = mainList.get(i).getDescription();
				writer.write(toWrite + "\r\n");
			}

			writer.close();
		} catch (IOException ioe) {
			showUserIOException();
		}
	}

	private void appendToFile(Task taskToAdd) {
		try {
			FileWriter writer = new FileWriter(fileName,true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        bufferedWriter.write(taskToAdd.getDescription() + "\r\n");

	        bufferedWriter.close();
			writer.close();

		} catch (IOException ioe) {
			showUserIOException();
		}
	}

	private static void showUserIOException() {
		System.out.println(MESSAGE_IOEXCEPTION_ERROR);
	}

	public ArrayList<Task> addTask(String description) {
		Task newTask = new Task(description);
		mainList.add(newTask);
		appendToFile(newTask);

		return mainList;
	}

	/*
	 * This method returns a empty arraylist if the taskID is invalid
	 * else, it returns the mainList arraylist
	 */
	public ArrayList<Task> deleteTask(int taskID) {
	    boolean foundTask = false;

	    for (int i=0; i<mainList.size(); i++) {
	        if (!foundTask && mainList.get(i).getID()==taskID) {
	            mainList.remove(i);
	            foundTask = true;
	        }
	    }
		if (!foundTask) {
			return new ArrayList<Task>();
		} else {
			writeToFile();
		}
		return mainList;
	}

	/*
	 * This method returns a empty arraylist if the taskID is invalid
	 * else, it returns the mainList arraylist
	 */
	public ArrayList<Task> editTask(int taskID, String newDescription) {
        boolean foundTask = false;

        for (int i=0; i<mainList.size(); i++) {
            if (!foundTask && mainList.get(i).getID()==taskID) {
                mainList.get(i).setDescription(newDescription);
                foundTask = true;
            }
        }
        if (!foundTask) {
            return new ArrayList<Task>();
        } else {
            writeToFile();
        }

		return mainList;
	}

	/*
	 * This method returns a empty arraylist if there is no such keyword
	 * else, it returns the searchResults arraylist
	 */
	public ArrayList<Task> searchTask(String keyword) {
	    searchResults.clear();
		for (int i=0; i<mainList.size(); i++) {
			if (mainList.get(i).getDescription().contains(keyword)) {
				searchResults.add(mainList.get(i));
			}
		}

		return searchResults;
	}
	
	/*
	 * This method is executed when the user wants to save the 
	 * data to a specific file, providing the new fileName
	 */
	public void saveToFile(String userFileName) {
		fileName = userFileName;
		writeToFile();
	}
	
	/*
	 * This method is executed when the user wants to save the 
	 * data to a specific directory and file, 
	 * providing the new fileName and directory to save it
	 * This method will check if the directory is valid
	 * If the directory is invalid, it will return an error message
	 * else, the new file with the user's file name will be created in the directory
	 */
	public void saveToFileWithDirectory(String directory, String userFileName) {
		// check if the directory is valid
		File userDirectory = new File(directory);
		boolean isValid = userDirectory.isDirectory();
		if (!isValid) {
			showUserInvalidDirectory();
		} else {
			File userDirectoryAndName = new File(directory + "/" + userFileName);
			writeToFileForSaveCommand(userDirectoryAndName);
		}
	}

	private void showUserInvalidDirectory() {
		System.out.println(MESSAGE_INVALID_DIRECTORY);
	}

	private void writeToFileForSaveCommand(File userDirectoryAndName) {
		try {
			FileWriter writer = new FileWriter(userDirectoryAndName.getAbsoluteFile());
			for (int i=0; i<mainList.size(); i++) {
				String toWrite = mainList.get(i).getDescription();
				writer.write(toWrite + "\r\n");
			}

			writer.close();
		} catch (IOException ioe) {
			showUserIOException();
		}
	}
	
	/*
	 * This method is executed when the user wants to load date 
	 * from a specific file, the user will provide the new fileName
	 * This method returns the new Arraylist<String> to replace the main list
	 * This method will also include ways to convert string to task to
	 * execute other commands
	 */
	public ArrayList<Task> loadFileWithFileName(String userFileName) {
		File file = new File(userFileName);
		ArrayList<String> newMainList = new ArrayList<String>();
		boolean isValid = file.exists();
		if (isValid) {
			Scanner sc;
			try {
				sc = new Scanner(file);
				while (sc.hasNextLine()) {
					newMainList.add(sc.nextLine());
				}
			} catch (FileNotFoundException e) {
				System.out.println("File Not Found");
			}
		}
		
		ArrayList<Task> updatedMainList = convertStringToTask(newMainList);
		
		return updatedMainList;
	}
	
	/*
	 * This method converts ArrayList<String> read from file 
	 * to ArrayList<Task> to allow execution of other commands
	 */
	private ArrayList<Task> convertStringToTask(ArrayList<String> newMainList) {
		ArrayList<Task> updatedMainList = new ArrayList<Task>();
		for (int i=0; i<newMainList.size(); i++) {
			Task newTask = new Task(newMainList.get(i));
			updatedMainList.add(newTask);
		}
		
		return updatedMainList;
	}

	public ArrayList<Task> getMainList() {
		return mainList;
	}
}
