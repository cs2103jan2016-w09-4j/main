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

    // for default file to store saved file names
    private static String defaultFile;

    // To store all file names
    private static ArrayList<String> storeFileNames;

    // default file name is "mytextfile.txt"
    public Storage() {
        fileName = "mytextfile.txt";
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
    public ArrayList<String> getFileNameList() throws IOException {

        File file = new File(defaultFile);
        boolean isCreated = file.createNewFile();

        // created file, for first time use
        if (isCreated) {
            FileWriter out = new FileWriter(file);
            out.write(fileName + "\r\n");
            storeFileNames.add(fileName);

            out.close();

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
   public ArrayList<Task> getMostRecentList(ArrayList<String> listOfFileNames) throws IOException {
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
			fileName = recentFileName;

			try {
				recentList = loadFileWithFileName(recentFileName);
			} catch (FileNotFoundException e) {
					
				// if file not found / deleted, load mytextfile.txt
				try {

					recentList = loadDefaultTextFile(defaultFileName);
						
				} catch (FileNotFoundException e1) {
						
					// if default file does no exist, return empty array list
					//change back file name to default
					fileName = defaultFileName;		
						
					//write "mytextfile.txt" into default file					
					writeToDefaultFile(defaultFileName);
						
					return recentList;
						
				}
			}

		} else {
			recentDirectory = splitLine[0];
			recentFileName = getFileName(mostRecentFile, recentDirectory);
			fileName = recentFileName;

			try {
				recentList = loadFileWithDirectory(recentDirectory, recentFileName);
					
			} catch (NotDirectoryException | FileNotFoundException e) {
					
				//if file not found or cannot find directory
				try {
						
					recentList = loadDefaultTextFile(defaultFileName);
					
				} catch (FileNotFoundException e1) {
					// if default file does no exist, return empty array list
					//change back file name to default
					fileName = defaultFileName;
						
					//write "mytextfile.txt" into default file
					writeToDefaultFile(defaultFileName);
										
					return recentList;
				}
			}
		}
	}

	return recentList;
    }
	
	/* In cases where the most recent file is not found or does not exist
	 * It will return the list from the default file - 'mytextfile.txt'
	 */
	private ArrayList<Task> loadDefaultTextFile(String defaultFileName) throws FileNotFoundException, IOException {
		ArrayList<Task> recentList;
		recentList = loadFileWithFileName(defaultFileName);
		fileName = defaultFileName; 	//change back file name to default
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
    public static String getFileName(String line, String toReplace) {
        String lineWithoutDirectory = line.replace(toReplace, "").trim();

        return lineWithoutDirectory;
    }

    // ================================================================================
    // Writing of Files
    // ================================================================================

    // rewrite whole file
    public void writeToFile() throws IOException {
        FileWriter writer = new FileWriter(fileName);
        writeTasksFromMainList(writer);
    }

    private void writeTasksFromMainList(FileWriter writer) throws IOException {
        for (int i = 0; i < mainList.size(); i++) {
            Task taskToAdd = mainList.get(i);

            String taskDescription = taskToAdd.getDescription();
            writer.write(taskDescription + "\r\n");
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

            String toWrite = directory + " , " + userFileName;
            writeToDefaultFile(toWrite);
        }
    }

    // write directory and filename to default file to keep track
    public void writeToDefaultFile(String toWrite) throws IOException {
        FileWriter writer = new FileWriter(defaultFile, true);
        writer.write(toWrite + "\r\n");
        writer.close();
    }

    /*
     * This method loads data from a specific file. This method will throw an
     * exception if the file does not exist
     */
    public ArrayList<Task> loadFileWithFileName(String userFileName) throws FileNotFoundException {

        File file = new File(userFileName);
        ArrayList<String> listFromFile = new ArrayList<String>();
        boolean isValid = file.exists();

        if (isValid) {
            readFileWhenFileExists(file, listFromFile);

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
            throws FileNotFoundException, NotDirectoryException {

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
    private ArrayList<Task> convertStringToTask(ArrayList<String> listFromFile) {
        ArrayList<Task> updatedMainList = new ArrayList<Task>();

        for (int i = 0; i < listFromFile.size(); i++) {
            String getLineFromFile = listFromFile.get(i);
            Task newTask = new Task(getLineFromFile);

            updatedMainList.add(newTask);
        }

        return updatedMainList;
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
