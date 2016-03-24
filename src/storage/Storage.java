package storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.text.ParseException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import common.Task;

public class Storage {

	private static ArrayList<Task> mainList;
	private static String fileName;
	private static String savedDirectory;

	// for default file to store saved file names
	private static String defaultFile;

	// To store all file names
	private static ArrayList<String> storeFileNames;

	// default file name is "mytextfile.txt"
	public Storage() {
		fileName = "mytextfile.txt";
		savedDirectory = "";
		mainList = new ArrayList<Task>();
		defaultFile = "defaultfile.txt";
		storeFileNames = new ArrayList<String>();
	}

	/*
	 * This method returns the main list to Logic The most recent main list from
	 * the last saved file will be returned If this is the first time the user
	 * is using, it will return an empty ArrayList If the user did not save to
	 * any file, it will return the list from the default file
	 */
	public ArrayList<Task> getMainList() {
		ArrayList<String> fileNameList;
		ArrayList<Task> recentTaskList = new ArrayList<Task>();

		try {
			fileNameList = getFileNameList();
			recentTaskList = getMostRecentList(fileNameList);

		} catch (IOException e) {

			// do nothing
			// will end up returning empty arraylist
		}

		if (!recentTaskList.isEmpty()) {
			setMainList(recentTaskList);
		}

		return mainList;
	}

	// get file names from default file
	private ArrayList<String> getFileNameList() throws IOException {

		File file = new File(defaultFile);
		boolean isCreated = file.createNewFile();

		// created file, for first time use
		if (isCreated) {
			FileWriter out = new FileWriter(file);
			out.write(fileName + "\r\n");
			storeFileNames.add(fileName);

			FileWriter out2 = new FileWriter(fileName);

			out.close();
			out2.close();

			// file already exist, read from file
		} else {
			Scanner scanner;
			scanner = readFileNames(file);

			scanner.close();
		}

		return storeFileNames;
	}

	private Scanner readFileNames(File file) throws FileNotFoundException {
		Scanner scanner;
		scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			storeFileNames.add(scanner.nextLine());
		}
		return scanner;
	}

	// update most recent list
	private ArrayList<Task> getMostRecentList(ArrayList<String> listOfFileNames) throws IOException {
		String recentFileName;
		String recentDirectory;
		String defaultFileName = "mytextfile.txt";
		ArrayList<Task> recentList = new ArrayList<Task>();

		if (!listOfFileNames.isEmpty()) {
			String mostRecentFile = getMostRecentFileName(listOfFileNames);

			// check if there is directory
			String[] splitLine = mostRecentFile.split(" , ");

			// consist only the filename
			if (splitLine.length == 1) {
				recentFileName = splitLine[0];

				try {
					recentList = loadFileWithFileName(recentFileName);
					fileName = recentFileName; // change file name if it exist

				} catch (FileNotFoundException | ParseException e) {

					// if file not found / deleted, load mytextfile.txt
					try {

						recentList = loadDefaultTextFile(defaultFileName);
						// write "mytextfile.txt" into default file
						writeToDefaultFile(defaultFileName);

					} catch (FileNotFoundException | ParseException e1) {

						// if default file does no exist, return empty list

						return recentList;

					}
				}

			} else {
				recentDirectory = splitLine[0];
				recentFileName = getFileName(mostRecentFile, recentDirectory);

				try {
					recentList = loadFileWithDirectory(recentDirectory, recentFileName);
					// change filename and directory if it exist
					fileName = recentFileName;
					savedDirectory = recentDirectory;

				} catch (NotDirectoryException | FileNotFoundException | ParseException e) {

					try {

						// load list from default text file ("mytextfile.txt")
						recentList = loadDefaultTextFile(defaultFileName);
						writeToDefaultFile(defaultFileName); 
						// write "mytextfile.txt" into default file

					} catch (FileNotFoundException | ParseException e1) {
						// if default file does no exist, return empty array
						// list
						return recentList;
					}
				}
			}
		}

		return recentList;
	}

	/*
	 * In cases where the most recent file is not found or does not exist It
	 * will return the list from the default file - 'mytextfile.txt'
	 */
	private ArrayList<Task> loadDefaultTextFile(String defaultFileName) throws FileNotFoundException, IOException, ParseException {
		ArrayList<Task> recentList;
		recentList = loadFileWithFileName(defaultFileName);
		fileName = defaultFileName; // change back file name to default
		writeToDefaultFile(defaultFileName);
		return recentList;

	}

	// This method obtains the most recent file name
	private String getMostRecentFileName(ArrayList<String> listOfFileNames) {
		int lastIndex = listOfFileNames.size() - 1;
		String mostRecentFile = listOfFileNames.get(lastIndex);
		return mostRecentFile;
	}

	// Obtain filename without directory
	private static String getFileName(String line, String directoryToReplace) {
		String toReplace = directoryToReplace + " , ";
		String lineWithoutDirectory = line.replace(toReplace, "").trim();

		return lineWithoutDirectory;
	}

	// ================================================================================
	// Writing of Files
	// ================================================================================

	/*
	 * This methods overwrites the data in the file This method will check if
	 * the directory of the file changed
	 */
	public void writeToFile() throws IOException {

		if (savedDirectory.isEmpty()) {

			FileWriter writer = new FileWriter(fileName);
			writeTasksFromMainList(writer);

		} else {
			// if the most recent file is from another directory
			// write to this directory and file
			File accessFile = new File(savedDirectory + "/" + fileName);
			FileWriter writer = new FileWriter(accessFile.getAbsoluteFile());
			writeTasksFromMainList(writer);
		}
	}

	private void writeTasksFromMainList(FileWriter writer) throws IOException {
		/*for (int i = 0; i < mainList.size(); i++) {
			Task taskToAdd = mainList.get(i);
			String taskDescription = taskToAdd.getDescription();
			writer.write(taskDescription + "\r\n");
		}

		writer.close();*/
		
		ArrayList<String> allTaskToString = convertTaskToString(mainList);
		for (int i=0; i<allTaskToString.size(); i++) {
			String lineToWrite = allTaskToString.get(i);
			writer.write(lineToWrite + "\r\n");
		}
		
		writer.close();
	}
	
	public void appendToFile(Task taskToAdd) throws IOException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.add(taskToAdd);
		ArrayList<String> stringList = convertTaskToString(taskList);
		
		
		if (savedDirectory.isEmpty()) {
			FileWriter writer = new FileWriter(fileName, true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			String lineToAppend = stringList.get(0);
			bufferedWriter.write(lineToAppend + "\r\n");
			
			bufferedWriter.close();
			writer.close();
			
		} else {
			File accessFile = new File(savedDirectory + "/" + fileName);
			FileWriter writer = new FileWriter(accessFile.getAbsoluteFile(),true);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			String lineToAppend = stringList.get(0);
			bufferedWriter.write(lineToAppend + "\r\n");
			
			bufferedWriter.close();
			writer.close();
			//writeTasksFromMainList(writer);	
		}

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

		// write to default file to load recent
		writeToDefaultFile(userFileName);
	}

	/*
	 * This method is executed when the user wants to save the data to a
	 * specific directory and file. This method will check if the directory is
	 * valid. If the directory is invalid, it will return an error message else,
	 * the new file will be created in the directory
	 */
	public void saveToFileWithDirectory(String directory, String userFileName)
			throws IOException, NotDirectoryException {

		// check if the directory is valid
		File userDirectory = new File(directory);
		boolean isValid = userDirectory.isDirectory();

		if (!isValid) {
			throw new NotDirectoryException(userDirectory.getName());
		} else {
			File userDirectoryAndName = new File(directory + "/" + userFileName);
			FileWriter writer = new FileWriter(userDirectoryAndName.getAbsoluteFile());
			writeTasksFromMainList(writer);

			// write to default file to load most recent
			String toWrite = directory + " , " + userFileName;
			writeToDefaultFile(toWrite);

			// set new file name and directory
			fileName = userFileName;
			savedDirectory = directory;
		}
	}

	// write directory and filename to default file to keep track of recent file
	private void writeToDefaultFile(String toWrite) throws IOException {
		FileWriter writer = new FileWriter(defaultFile, true);
		writer.write(toWrite + "\r\n");
		writer.close();
	}

	/*
	 * This method loads data from a specific file. This method will throw an
	 * exception if the file does not exist
	 */
	public ArrayList<Task> loadFileWithFileName(String userFileName) throws IOException, FileNotFoundException, ParseException {

		File file = new File(userFileName);
		ArrayList<String> listFromFile = new ArrayList<String>();
		boolean isValid = file.exists();

		if (isValid) {
			readFileWhenFileExists(file, listFromFile);

			fileName = userFileName; // update filename for future writing of
										// data
			writeToDefaultFile(fileName);
		} else if (!isValid) {
			throw new FileNotFoundException();
		}

		ArrayList<Task> updatedMainList = convertStringToTask(listFromFile);
		mainList.clear();
		updateMainList(updatedMainList);

		return mainList;
	}

	private void readFileWhenFileExists(File file, ArrayList<String> listFromFile) throws FileNotFoundException {
		Scanner sc;
		sc = new Scanner(file);
		while (sc.hasNextLine()) {
			listFromFile.add(sc.nextLine());
		}

		sc.close();
	}

	/*
	 * This method loads data from a specific directory and file. This method
	 * will check if the directory and file exists. If the directory or file
	 * does not exist, it will throw an exception
	 */

	public ArrayList<Task> loadFileWithDirectory(String directory, String userFileName)
			throws IOException, FileNotFoundException, NotDirectoryException, ParseException {

		ArrayList<String> listFromLoadFile = new ArrayList<String>();

		// check if the directory is valid
		File userDirectory = new File(directory);
		boolean isDirectoryValid = userDirectory.isDirectory();

		// If directory is not valid, throw exception
		if (!isDirectoryValid) {
			throw new NotDirectoryException(userDirectory.getName());

		} else {
			File userDirectoryAndName = new File(directory + "/" + userFileName);
			boolean isFileValid = userDirectoryAndName.exists();

			if (isFileValid) {
				readFileWhenFileExists(userDirectoryAndName, listFromLoadFile);

				// update filename and directory
				fileName = userFileName;
				savedDirectory = directory;

				// write to default file to load most recent
				String toWrite = directory + " , " + userFileName;
				writeToDefaultFile(toWrite);

			} else if (!isFileValid) {
				throw new FileNotFoundException();
			}
		}

		ArrayList<Task> updatedMainListFromLoad = convertStringToTask(listFromLoadFile);
		mainList.clear();
		// transfer contents from file to main list
		updateMainList(updatedMainListFromLoad);
		setMainList(mainList);

		return mainList;
	}

	/*
	 * This method converts ArrayList<String> read from file to ArrayList<Task>
	 * to allow execution of other commands
	 */
	/*private ArrayList<Task> convertStringToTask(ArrayList<String> listFromFile) {
		ArrayList<Task> updatedMainList = new ArrayList<Task>();

		for (int i = 0; i < listFromFile.size(); i++) {
			String getLineFromFile = listFromFile.get(i);
			Task newTask = new Task(getLineFromFile);

			updatedMainList.add(newTask);
		}

		return updatedMainList;
	}*/

	// This method converts String to Task to allow execution of other commands
	public static ArrayList<Task> convertStringToTask(ArrayList<String> stringList)
			throws ParseException {
		Task task1 = new Task("");
		String description, start, end, category;
		String withoutDescription, lineToReplace;
		ArrayList<Task> taskList = new ArrayList<Task>();
		
		for (int i = 0; i < stringList.size(); i++) {
			String line = stringList.get(i);
			
			// check existence
			int indexOfFrom = line.indexOf("From");
			int indexOfTo = line.indexOf("To");
			int indexOfCategory = line.indexOf('#');
			
			// only have description eg: meeting
			if (indexOfFrom == -1 && indexOfTo == -1 && indexOfCategory == -1) {

				task1 = new Task(line);

			} else {

				if (indexOfFrom == -1) {
					// means no start date

					// check for end date
					if (indexOfTo == -1) {
						// means no end date

						// only have description and category eg: meeting #work
						// #school

						// get description
						description = line.substring(0, indexOfCategory);
						task1 = setDescription(description);

						// get category
						lineToReplace = description;
						category = getFields(line, lineToReplace); 
						// get: #work #school

						setMultipleCategories(task1, category);

					} else {
						// have end date eg: meeting To 22/03/2016 13:00

						// get description
						description = line.substring(0, indexOfTo);
						task1 = setDescription(description);

						// get end date
						lineToReplace = description;
						withoutDescription = getFields(line, lineToReplace); 
						// get: To 22/03/2016 13:00

						// check category
						if (indexOfCategory != -1) {
							// have category eg: To 22/03/2016 13:00 #school #work
							indexOfCategory = withoutDescription.indexOf("#");
							end = withoutDescription.substring(3, indexOfCategory);
							task1.setEnd(end.trim());

							category = withoutDescription.substring(indexOfCategory, withoutDescription.length());
							setMultipleCategories(task1, category);

						} else {
							// means no category eg: To 22/03/2016 13:00
							end = withoutDescription.substring(3, withoutDescription.length());
							task1.setEnd(end);
						}
					}

					// have start time
				} else if (indexOfFrom != -1) {

					description = line.substring(0, indexOfFrom);
					task1 = setDescription(description);

					// check for end date
					if (indexOfTo == -1) {
						// means no end date eg: meeting From 22/03/2016 08:00

						// check category
						if (indexOfCategory == -1) {
							// means no category eg: meeting From 22/03/2016 08:00
							withoutDescription = getFields(line, description); 
							// get: From 22/03/2016 08:00
							start = withoutDescription.substring(5, withoutDescription.length());
							task1.setStart(start);

						} else {
							// have category eg: meeting From 22/03/2016 08:00 #school #work
							withoutDescription = getFields(line, description); 
							// get: From 22/03/2016 08:00 #school #work

							// get start
							indexOfCategory = withoutDescription.indexOf("#");
							start = withoutDescription.substring(5, indexOfCategory);
							task1.setStart(start.trim());

							// get category
							lineToReplace = "FROM " + start;
							category = getFields(withoutDescription, lineToReplace); 
							// get: #school #work
							setMultipleCategories(task1, category);
						}

					} else {
						// have end date eg: meeting From 22/03/2016 08:00 To 22/03/2016 13:00

						withoutDescription = getFields(line, description); 
						// get: From 22/03/2016  08:00 To  22/03/2016 13:00

						// get start time
						indexOfTo = withoutDescription.indexOf("TO");
						start = withoutDescription.substring(5, indexOfTo);
						task1.setStart(start.trim());

						// check category
						if (indexOfCategory == -1) {
							// means no category eg: meeting From 22/03/2016
							// 08:00 To 22/03/2016 13:00

							// get end time
							lineToReplace = "FROM " + start;
							String onlyEnd = getFields(withoutDescription, lineToReplace); 
							// get: To 22/03/2016 13:00
							end = onlyEnd.substring(3, onlyEnd.length());
							task1.setEnd(end);

						} else {
							// have category eg: meeting From 22/03/2016 08:00
							// To 22/03/2016 13:00 #school #work

							// get end time
							lineToReplace = "FROM " + start;
							String onlyEnd = getFields(withoutDescription, lineToReplace); 
							// get: To 22/03/2016 13:00 #work #school

							indexOfCategory = onlyEnd.indexOf('#');
							end = onlyEnd.substring(3, indexOfCategory);
							task1.setEnd(end.trim());

							// get category
							lineToReplace = "TO " + end;
							category = getFields(onlyEnd, lineToReplace); 
							// get: #school #work
							setMultipleCategories(task1, category);
						}
					}
				}
			}

			taskList.add(task1);
		}

		return taskList;

	}

	private static Task setDescription(String description) {
		Task task1;
		task1 = new Task(description.trim());
		return task1;
	}

	private static void setMultipleCategories(Task task1, String category) {
		// if there are multiple categories
		String categoryName;
		String name;
		String[] multipleCategories = category.split(" ");
		for (int j = 0; j < multipleCategories.length; j++) {
			categoryName = multipleCategories[j];
			name = categoryName.substring(1, categoryName.length());
			task1.setCategory(name);
		}
	}

	// This method converts Task to String object for writing purpose
	public static ArrayList<String> convertTaskToString(ArrayList<Task> taskList) {
		String line1 = null, line2 = null;
		String categoryLine = "";
		String description, start = null, end = null;
		String indicator;
		boolean isCategoryEmpty = false;
		Task task1;
		ArrayList<String> stringList = new ArrayList<String>();
		
		for (int i = 0; i < taskList.size(); i++) {
			
			task1 = taskList.get(i);
			categoryLine = "";
			description ="";
			start = "";
			end = "";
			
			// get description from task
			description = task1.getDescription();

			// get start from task

			if (task1.getStartDateString().isEmpty()) {
				indicator = "-";
			} else {
				start = task1.getStartDateString();
				indicator = "+";
			}

			if (task1.getEndDateString().isEmpty()) {
				indicator = indicator + "-";
			} else {
				end = task1.getEndDateString();
				indicator = indicator + "+";
			}

			ArrayList<String> categoryList = task1.getCategories();
			
			if (categoryList.isEmpty()) {
				indicator = indicator + "-";
				isCategoryEmpty = false;
					
			} else {

				isCategoryEmpty = true;
				indicator = indicator + "+";

				// only one category
				if (categoryList.size() == 1) {
					line2 = " #" + categoryList.get(0);
				} else {
					int count = 0;
					while (count < categoryList.size()) {
						categoryLine = categoryLine + " #" + categoryList.get(count);
						count++;
					}

					line2 = categoryLine;
				}
			}

			if (!start.isEmpty() && !end.isEmpty()) {
				line1 = indicator + " " + description + " FROM " + start + " TO " + end;
			} else if (!start.isEmpty() && end.isEmpty()) {
				line1 = indicator + " " + description + " FROM " + start;
			} else if (start.isEmpty() && !end.isEmpty()) {
				line1 = indicator + " " + description + " TO " + end;
			} else if (start.isEmpty() && end.isEmpty()) {
				line1 = indicator + " " + description;
			}

			String finalLine = "";

			if (isCategoryEmpty) {
				finalLine = line1 + line2;
			} else {
				finalLine = line1;
			}

			stringList.add(finalLine);
		}
		return stringList;
	}

	private static String getFields(String line, String toReplace) {
		String lineWithoutDirectory = line.replace(toReplace, "").trim();

		return lineWithoutDirectory;
	}

	// for load commands
	public void setMainList(ArrayList<Task> mainList) {
		Storage.mainList = mainList;
	}

	/*
	 * This method adds the data from file to main list to update For load
	 * commands
	 */
	private void updateMainList(ArrayList<Task> dataFromFile) {
		for (int j = 0; j < dataFromFile.size(); j++) {
			mainList.add(dataFromFile.get(j));
		}
	}

}
