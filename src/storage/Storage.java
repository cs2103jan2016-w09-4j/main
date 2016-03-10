package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import common.Task;

public class Storage {

	private static ArrayList<Task> mainList;
	private static String fileName;

	// default file name is "mytextfile.txt"
	public Storage() {
		fileName = "mytextfile.txt";
		mainList = new ArrayList<Task>();
	}
		
	// ================================================================================
	// Writing of Files
	// ================================================================================

	// rewrite whole file
	public void writeToFile() throws IOException {
			FileWriter writer = new FileWriter(fileName);
			for (int i = 0; i < mainList.size(); i++) {
				String toWrite = mainList.get(i).getDescription();
				writer.write(toWrite + "\r\n");
			}
			
			writer.close();
	}

	public void appendToFile(Task taskToAdd) throws IOException {
			FileWriter writer = new FileWriter(fileName, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(taskToAdd.getDescription() + "\r\n");

			bufferedWriter.close();
			writer.close();

	}

	// ================================================================================
	// Saving and Loading commands
	// ================================================================================

	/*
	 * This method is executed when the user wants to save the data to a
	 * specific file. The previous data in the file will be overwritten
	 */
	public void saveToFile(String userFileName) throws IOException {
		fileName = userFileName;
		writeToFile();
	}

	/*
	 * This method is executed when the user wants to save the data to a
	 * specific directory and file. This method will check if the directory is
	 * valid. If the directory is invalid, it will return an error message else,
	 * the new file will be created in the directory
	 */
	public void saveToFileWithDirectory(String directory, String userFileName) throws IOException, NotDirectoryException {

		// check if the directory is valid
		File userDirectory = new File(directory);
		boolean isValid = userDirectory.isDirectory();

		if (!isValid) {
			throw new NotDirectoryException(userDirectory.getName());
		} else {
			File userDirectoryAndName = new File(directory + "/" + userFileName);
			FileWriter writer = new FileWriter(userDirectoryAndName.getAbsoluteFile());
			for (int i = 0; i < mainList.size(); i++) {
				String toWrite = mainList.get(i).getDescription();
				writer.write(toWrite + "\r\n");
			}

			writer.close();
		}
	}

	/*
	 * This method loads data from a specific file.
	 * This method will throw an exception if the file does not exist
	 */
	public ArrayList<Task> loadFileWithFileName(String userFileName) throws FileNotFoundException, NotDirectoryException {
		
		File file = new File(userFileName);
		ArrayList<String> listFromFile = new ArrayList<String>();
		boolean isValid = file.exists();

		if (isValid) {
			Scanner sc;
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				listFromFile.add(sc.nextLine());
			}

			sc.close();
		}

		ArrayList<Task> updatedMainList = convertStringToTask(listFromFile);
		mainList.clear();
		updateMainList(updatedMainList);
		
		return mainList;
	}

	/*
	 * This method loads data from a specific directory and file. 
	 * This method will check if the directory and file exists.
	 * If the directory or file does not exist, it will throw an exception 
	 */

	public ArrayList<Task> loadFileWithDirectory(String directory, String userFileName) throws FileNotFoundException, NotDirectoryException {


		ArrayList<String> listFromLoadFile = new ArrayList<String>();

		// check if the directory is valid
		File userDirectory = new File(directory);
		boolean isDirectoryValid = userDirectory.isDirectory();

		if (!isDirectoryValid) {
			throw new NotDirectoryException(userDirectory.getName());

		} else {
			File userDirectoryAndName = new File(directory + "/" + userFileName);
			boolean isFileValid = userDirectoryAndName.exists();
			if (isFileValid) {
				Scanner sc;
				sc = new Scanner(userDirectoryAndName);
				while (sc.hasNextLine()) {
					listFromLoadFile.add(sc.nextLine());
				}
				
				sc.close();
			}
		}

		ArrayList<Task> updatedMainListFromLoad = convertStringToTask(listFromLoadFile);
		mainList.clear();
		updateMainList(updatedMainListFromLoad);
		setMainList(mainList);
		
		return mainList;
	}

	/*
	 * This method converts ArrayList<String> read from file to ArrayList<Task>
	 * to allow execution of other commands
	 */
	private ArrayList<Task> convertStringToTask(ArrayList<String> listFromFile) {
		ArrayList<Task> updatedMainList = new ArrayList<Task>();

		for (int i = 0; i < listFromFile.size(); i++) {
			Task newTask = new Task(listFromFile.get(i));
			updatedMainList.add(newTask);
		}

		return updatedMainList;
	}
	
	public ArrayList<Task> getMainList(){
		return mainList;
	}
	
	// for load commands
	public void setMainList(ArrayList<Task> mainList){
		Storage.mainList = mainList;
	}
	
	/*
	 * This method adds the data from file to update the main list
	 * For load commands
	 */
	private void updateMainList(ArrayList<Task> dataFromFile) {
		for (int j = 0; j < dataFromFile.size(); j++) {
			mainList.add(dataFromFile.get(j));
		}
	}
}
