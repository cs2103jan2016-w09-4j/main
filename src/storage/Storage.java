package storage;

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

	private static final String HEADER_CURRENT = "CURRENT TASKS:";
	private static final String HEADER_COMPLETED = "COMPLETED TASKS:";
	private static final String DEFAULT_FILE = "DefaultFile.txt";
	private static final String DEFAULT_TEXTFILE = "MyTasks.txt";

	private static final int NUMBER_OF_FIELDS = 4;

	private static final String FIELDS_DESCRIPTION = "Description:";
	private static final String FIELDS_START = "Start:";
	private static final String FIELDS_END = "End:";
	private static final String FIELDS_CATEGORY = "Category:";
	private static final String FIELDS_CATEGORY_INDICATOR = "#";
	private static final String FIELDS_BREAKLINE = "-----------------------------------";
	private static final String FIELDS_HEADERLINE = "=================";

	private static ArrayList<Task> mainList;
	private static ArrayList<Task> completedList;

	private static String fileName;
	private static String savedDirectory;

	// for default file to store saved file names
	private static String defaultFile;

	// To store all file names
	private static ArrayList<String> storeFileNames;

	// default file name is "MyTasks.txt"
	public Storage() {
		fileName = DEFAULT_TEXTFILE;
		defaultFile = DEFAULT_FILE;
		savedDirectory = "";

		mainList = new ArrayList<Task>();
		completedList = new ArrayList<Task>();
		storeFileNames = new ArrayList<String>();

		// Create default file for load recent feature
		try {
			File file = new File(defaultFile);
			boolean isCreated;
			isCreated = file.createNewFile();
			// created file, for first time use
			if (isCreated) {
				// Create default file
				FileWriter out = new FileWriter(file);
				out.write(fileName + "\r\n");
				storeFileNames.add(fileName);

				// create "MyTasks.txt"
				FileWriter out2 = new FileWriter(fileName);

				out.close();
				out2.close();
			}
		} catch (IOException e) {
		}
	}

	/*
	 * This method returns the main list to Logic. The most recent main list
	 * from the last saved file will be returned. If this is the first time the
	 * user is using, it will return an empty ArrayList. If the user did not
	 * save to any file, it will return the list from the default file
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

		setMainList(recentTaskList);

		return mainList;
	}

	// Returns completed list to Logic
	public ArrayList<Task> getCompletedList() {
		return completedList;
	}

	// get file names from default file
	private ArrayList<String> getFileNameList() throws IOException {

		File file = new File(defaultFile);
		Scanner scanner;
		scanner = readFileNames(file);
		scanner.close();

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
		String recentFileName, recentDirectory;
		String defaultFileName = DEFAULT_TEXTFILE;
		ArrayList<Task> recentList = new ArrayList<Task>();

		String mostRecentFile = getMostRecentFileName(listOfFileNames);

		// check if there is directory
		String[] splitLine = mostRecentFile.split(" , ");

		// consist only the filename
		if (splitLine.length == 1) {
			recentFileName = splitLine[0];

			try {
				recentList = loadFileWithFileName(recentFileName);

			} catch (FileNotFoundException | ParseException e) {
				// if file not found / deleted, load MyTasks.txt
				try {
					recentList = loadDefaultTextFile(defaultFileName);
					// write "MyTasks.txt" into default file
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

			} catch (NotDirectoryException | FileNotFoundException | ParseException e) {

				try {
					// load list from default text file ("MyTasks.txt")
					recentList = loadDefaultTextFile(defaultFileName);
					writeToDefaultFile(defaultFileName);
					// write "MyTasks.txt" into default file

				} catch (FileNotFoundException | ParseException e1) {
					// if default file does no exist, return empty list
					return recentList;
				}
			}
		}

		return recentList;
	}

	/*
	 * In cases where the most recent file is not found or does not exist It
	 * will return the list from the default file - 'mytextfile.txt'
	 */
	private ArrayList<Task> loadDefaultTextFile(String defaultFileName)
			throws FileNotFoundException, IOException, ParseException {
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

			// Write heading for current tasks
			writeCurrentHeader(writer);

			// Write current tasks
			writeTasksFromMainList(writer);

			// Write heading for completed tasks
			writeCompletedHeader(writer);

			// Write completed tasks
			writeTaskFromCompletedList(writer);

			writer.close();

		} else {
			// if the most recent file is from another directory
			// write to this directory and file
			File accessFile = new File(savedDirectory + "/" + fileName);
			FileWriter writer = new FileWriter(accessFile.getAbsoluteFile());

			writeCurrentHeader(writer);
			writeTasksFromMainList(writer);
			writeCompletedHeader(writer);
			writeTaskFromCompletedList(writer);

			writer.close();
		}
	}

	// This method writes the "Current Task" header into the file
	private void writeCurrentHeader(FileWriter writer) throws IOException {
		writer.write(FIELDS_HEADERLINE + "\r\n");
		writer.write(HEADER_CURRENT + "\r\n");
		writer.write(FIELDS_HEADERLINE + "\r\n");
	}

	// This method writes the "Completed Task" header into the file
	private void writeCompletedHeader(FileWriter writer) throws IOException {
		writer.write("\r\n");
		writer.write(FIELDS_HEADERLINE + "\r\n");
		writer.write(HEADER_COMPLETED + "\r\n");
		writer.write(FIELDS_HEADERLINE + "\r\n");
	}

	private void writeTasksFromMainList(FileWriter writer) throws IOException {
		ArrayList<String> currentTaskToString = convertTaskToString(mainList);
		
		for (int i = 0; i < currentTaskToString.size(); i++) {
			String lineToWrite = currentTaskToString.get(i);
			writer.write(lineToWrite + "\r\n");
		}
	}

	private void writeTaskFromCompletedList(FileWriter writer) throws IOException {
		ArrayList<String> completedTaskToString = convertTaskToString(completedList);
		
		for (int i = 0; i < completedTaskToString.size(); i++) {
			String lineToWrite = completedTaskToString.get(i);
			//System.out.println("line to write is " + lineToWrite);
			writer.write(lineToWrite + "\r\n");
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
		FileWriter writer = new FileWriter(defaultFile);
		writer.write(toWrite + "\r\n");
		writer.close();
	}

	/*
	 * This method loads data from a specific file. This method will throw an
	 * exception if the file does not exist
	 */
	public ArrayList<Task> loadFileWithFileName(String userFileName)
			throws IOException, FileNotFoundException, ParseException {

		File file = new File(userFileName);
		ArrayList<String> listFromFile = new ArrayList<String>();
		boolean isValid = file.exists();

		if (isValid) {
			readFileWhenFileExists(file, listFromFile);

			// update filename for future writing
			fileName = userFileName;
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

	// ================================================================================
	// Methods used to convert types
	// ================================================================================

	// This method converts String to Task to allow execution of other commands
	public static ArrayList<Task> convertStringToTask(ArrayList<String> stringList) throws ParseException {

		Task currentTask = new Task("");
		String description, start, end, category;
		int countFields = 0;
		int indexOfCompletedTask = 0;
		ArrayList<Task> taskList = new ArrayList<Task>();

		for (int i = 0; i < stringList.size(); i++) {
			String line = stringList.get(i);

			// break line, contains at least 10 "-";
			if (line.contains("----------") || line.contains("==========") || line.isEmpty()) {
				// System.out.println("Encountered break line, continue");
				continue;

			} else if (line.equalsIgnoreCase(HEADER_CURRENT)) {
				continue;

			} else if (line.equalsIgnoreCase(HEADER_COMPLETED)) {
				// break as the current list all add to current ,main list
				indexOfCompletedTask = i + 2;
				break;
			} else {
				String[] splitLine = line.split(" ");
				String field = splitLine[0];

				// will always have description
				if (field.equals(FIELDS_DESCRIPTION)) {
					description = getFields(line, field);
					currentTask.setDescription(description);
					countFields++;

				} else if (field.equals(FIELDS_START)) {
					if (splitLine.length > 1) {
						start = getFields(line, field);
						currentTask.setStart(start);
					}
					countFields++;

				} else if (field.equals(FIELDS_END)) {
					if (splitLine.length > 1) {
						end = getFields(line, field);
						currentTask.setEnd(end);
					}
					countFields++;

				} else if (field.equals(FIELDS_CATEGORY)) {
					if (splitLine.length > 1) {
						category = getFields(line, field);
						setMultipleCategories(currentTask, category);
					}
					countFields++;
				}
			}

			if (countFields == NUMBER_OF_FIELDS) {
				taskList.add(currentTask);
				currentTask = new Task("");
				countFields = 0;
			}
		}

		addCompletedTask(stringList, indexOfCompletedTask);

		return taskList;
	}

	// This method stores the completed tasks in arraylist
	private static void addCompletedTask(ArrayList<String> stringList, int indexOfCompletedTask) throws ParseException {

		int countFields = 0;
		Task completedTask = new Task("");
		
		for (int j = indexOfCompletedTask; j < stringList.size(); j++) {
			String line = stringList.get(j);
			
			// break line, contains at least 10 "-";
			if (line.contains("----------") || line.contains("==========")) {
				// System.out.println("Encountered break line, continue");
				continue;

			} else {
				String[] splitLine = line.split(" ");
				String field = splitLine[0];

				// will always have description
				if (field.equals(FIELDS_DESCRIPTION)) {
					String description = getFields(line, field);
					completedTask.setDescription(description);
					countFields ++;

				} else if (field.equals(FIELDS_START)) {
					if (splitLine.length > 1) {
						String start = getFields(line, field);
						completedTask.setStart(start);
					}
					countFields++;

				} else if (field.equals(FIELDS_END)) {
					if (splitLine.length > 1) {
						String end = getFields(line, field);
						completedTask.setEnd(end);
					}
					countFields++;

				} else if (field.equals(FIELDS_CATEGORY)) {
					if (splitLine.length > 1) {
						String category = getFields(line, field);
						setMultipleCategories(completedTask, category);
					}
					countFields++;
				}
			}

			if (countFields == NUMBER_OF_FIELDS) {
				completedList.add(completedTask);
				completedTask = new Task("");
				countFields = 0;
			}
		}
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
		String linesOfCategory = "";
		String description, start = null, end = null;
		String descriptionLine, startLine, endLine, categoryLine;
		Task task1;
		ArrayList<String> stringList = new ArrayList<String>();

		for (int i = 0; i < taskList.size(); i++) {

			task1 = taskList.get(i);
			linesOfCategory = "";
			description = "";
			start = "";
			end = "";

			// get description from task
			description = task1.getDescription();
			descriptionLine = FIELDS_DESCRIPTION + " " + description;
			// get: Description: meeting

			// get start from task

			if (task1.getStartDateString().isEmpty()) {
				startLine = FIELDS_START + " ";

			} else {
				start = task1.getStartDateString();
				startLine = FIELDS_START + " " + start;

			}

			if (task1.getEndDateString().isEmpty()) {
				endLine = FIELDS_END + " ";

			} else {
				end = task1.getEndDateString();
				endLine = FIELDS_END + " " + end;
			}

			ArrayList<String> categoryList = task1.getCategories();

			if (categoryList.isEmpty()) {
				categoryLine = FIELDS_CATEGORY + " ";

			} else {

				// only one category
				if (categoryList.size() == 1) {
					categoryLine = FIELDS_CATEGORY + " " + FIELDS_CATEGORY_INDICATOR + categoryList.get(0);

				} else {
					int countCategory = 0;
					linesOfCategory = getMultipleCategories(linesOfCategory, categoryList, countCategory);
					categoryLine = FIELDS_CATEGORY + linesOfCategory;
				}
			}

			stringList.add(descriptionLine);
			stringList.add(startLine);
			stringList.add(endLine);
			stringList.add(categoryLine);

			String breakLine = FIELDS_BREAKLINE;
			stringList.add(breakLine);

		}
		return stringList;
	}

	private static String getMultipleCategories(String linesOfCategory, ArrayList<String> categoryList,
			int countCategory) {
		while (countCategory < categoryList.size()) {
			linesOfCategory = linesOfCategory + " " + FIELDS_CATEGORY_INDICATOR + categoryList.get(countCategory);
			countCategory++;
		}
		return linesOfCategory;
	}

	private static String getFields(String line, String toReplace) {
		String lineWithoutDirectory = line.replace(toReplace, "").trim();

		return lineWithoutDirectory;
	}

	/*
	 * This method adds the data from file to mainlist to update For load
	 * commands
	 */
	private void updateMainList(ArrayList<Task> dataFromFile) {
		for (int j = 0; j < dataFromFile.size(); j++) {
			mainList.add(dataFromFile.get(j));
		}
	}

	// ================================================================================
	// Setter Methods
	// ================================================================================

	/*
	 * This method is used when Logic make changes to the main list and pass the
	 * updated list to Storage This method is also used for load commands to
	 * give Logic the new main list
	 */
	public void setMainList(ArrayList<Task> mainList) {
		Storage.mainList = mainList;
	}

	public void setCompletedList(ArrayList<Task> completedList) {
		Storage.completedList = completedList;
	}

}
