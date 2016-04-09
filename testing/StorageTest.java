import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map.Entry;

import junitx.framework.FileAssert;

import org.junit.Before;
import org.junit.Test;

import common.Task;
import storage.Storage;

public class StorageTest {

	private Storage testStorage;
	private ArrayList<Task> mainList;
	private ArrayList<Task> getDataFromFile;
	private ArrayList<Task> completedList;
	private ArrayList<Entry<String, Integer>> autocompletionList;
	private String fileName1 = "MyTasks.txt";
	private String fileName2 = "newFile";
	private String fileName3 = "AutoCompletion.txt";
	private String defaultFileName = "DefaultFile.txt";
	private ArrayList<Task> recentList;

	private String outputException = "";
	// String expectedDirectory = "/Users/Documents/testDirectory";
	String expectedDirectory = "/Users/tanching/Dropbox/Y2S2/CS2103T/testDirectory";

	@Before
	public void init() throws IOException, ParseException {
		System.out.println("initializing");
		testStorage = new Storage();
		mainList = testStorage.getMainList();
		completedList = testStorage.getCompletedList();
		autocompletionList = testStorage.getAutoCompletionList();
		recentList = new ArrayList<Task>();
		recentList.addAll(mainList);

		getDataFromFile = new ArrayList<Task>();

		if (mainList.isEmpty()) {
			System.out.println("main list is empty");
		} else {
			System.out.println("Printing main list");
			printResult(mainList);
			System.out.println("Clear mainlist");
			mainList.clear();
		}

		if (autocompletionList.isEmpty()) {
			System.out.println("autocompletion list is empty");
		} else {
			System.out.println("Printing autocompletion list");
			printString(autocompletionList);
			System.out.println("Clear autocompletion list");
			autocompletionList.clear();
		}

		if (completedList.isEmpty()) {
			System.out.println("completedList is empty");
		} else {
			System.out.println("Printing completedList");
			printResult(completedList);
			System.out.println("Clear completedList");
			completedList.clear();
		}

		// Add task into mainList
		System.out.println("Adding tasks");
		// first task have all 3 fields
		Task task1 = new Task("meeting at night so late");
		// Task task1 = new Task("watch tv");
		task1.setStart("22/03/2016 13:00");
		task1.setEnd("22/03/2016 16:00");
		ArrayList<String> categoryToAdd = new ArrayList<String>();
		categoryToAdd.add("work");
		categoryToAdd.add("school");
		task1.setCategories(categoryToAdd);

		// have 2 fields
		Task task2 = new Task("gathering");
		task2.setEnd("25/03/2016 14:00");

		mainList.add(task1);
		mainList.add(task2);

		// adding task into completed list
		Task task3 = new Task("do tutorial");
		ArrayList<String> categoryToAdd2 = new ArrayList<String>();
		categoryToAdd2.add("homework");
		task3.setCategories(categoryToAdd2);

		// have 1 field
		Task task4 = new Task("meet friends");

		completedList.add(task3);
		completedList.add(task4);

		// add string as autocompletion
		String line1 = "add meeting now";
		String line2 = "gathering at 4pm";
		autocompletionList.add(new AbstractMap.SimpleEntry<String, Integer>(line1, 2));
		autocompletionList.add(new AbstractMap.SimpleEntry<String, Integer>(line2, 2));

		System.out.println("Print main list");
		printResult(mainList);
	}

	@Test
	public void writeToFile_ContentsOfAnotherFile_CorrectNumOfTasksAndContent() throws IOException {
		String readLine;
		int count;
		testStorage.writeToFile();

		// read file into arraylist
		File defaultTextFile = new File(fileName1);
		BufferedReader readFile = new BufferedReader(new FileReader(defaultTextFile));

		ArrayList<String> stringList = new ArrayList<String>();
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}

		try {
			getDataFromFile = testStorage.convertStringToTask(stringList, 0);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to convert String to Task");
			e.printStackTrace();
		}

		assertEquals(mainList.size(), getDataFromFile.size());

		count = 0;
		for (int i = 0; i < mainList.size(); i++) {
			Task task1 = mainList.get(i);
			Task task2 = getDataFromFile.get(i);

			if (task1.getDescription().equals(task2.getDescription())) {
				count++;
			}
		}
		// test content
		assertEquals(mainList.size(), count);
	}

	@Test
	public void writeAutoCompletionData_ContentsOfAnotherFile_CorrectNumOfStringandContent() throws IOException {
		testStorage.writeAutoCompletionData();

		String readLine;

		// read file into arraylist
		File defaultTextFile = new File(fileName3);
		BufferedReader readFile = new BufferedReader(new FileReader(defaultTextFile));

		ArrayList<String> stringList = new ArrayList<String>();
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}

		assertEquals(autocompletionList.size(), stringList.size());

		int count = 0;
		for (int i = 0; i < stringList.size(); i++) {
			String line1 = stringList.get(i);
			//System.out.println("line 1 from autocompletion is " + line1);
			String line2 = autocompletionList.get(i).getKey() + " : " + "Frequency : "
					+ autocompletionList.get(i).getValue();
			//System.out.println("key is " + line2);

			if (line1.equals(line2)) {
				count++;
			}
		}
		// test content
		assertEquals(autocompletionList.size(), count);
	}

	@Test
	public void saveToFileNoDirectory_NewFile_FileSaved() throws FileNotFoundException, IOException, ParseException {

		testStorage.saveToFile(fileName2);
		File file2 = new File(fileName1);
		File file3 = new File(fileName2);
		FileAssert.assertEquals(file2, file3);

		// file2.delete();
		file3.delete();
	}

	@Test
	public void loadFileWithFileName_DefaultTextFile_LoadListFromFile()
			throws FileNotFoundException, IOException, ParseException {

		File file1 = new File(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, mainList);

		file1.delete();
	}

	@Test
	public void loadFile_InvalidFileName_ExceptionThrown() throws IOException, ParseException {

		testStorage.saveToFile(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, mainList);

		// @Test testInvalidFileForLoading()
		// Equivalence partition:
		// test possible file names and invalid file names
		// loadFile
		try {
			testStorage.loadFileWithFileName("not a file");
		} catch (FileNotFoundException fnfe) {
			outputException = "Invalid File";
		}

		assertEquals("Invalid File", outputException);
	}

	// @Test
	public void saveToFileWithDirectory_NewFileAndDirectory_FileSaved() throws NotDirectoryException, IOException {
		testStorage.saveToFileWithDirectory(expectedDirectory, fileName2);

		// Read DefaultFile.txt to see if it is written correctly
		File defaultTextFile = new File(defaultFileName);
		Scanner scanner = new Scanner(defaultTextFile);
		String getRecentLine = scanner.nextLine();
		System.out.println("recent line from default file is " + getRecentLine);
		scanner.close();

		String expectedLine = expectedDirectory + " , " + fileName2;

		assertEquals(expectedLine, getRecentLine);

		defaultTextFile.delete();

		File file1 = new File(expectedDirectory + "/" + fileName2);
		File file2 = new File(fileName1);
		FileAssert.assertEquals(file2, file1);

	}

	// @Test
	public void loadFileWithDirectory_NewFileAndDirectory_LoadNewFile()
			throws NotDirectoryException, FileNotFoundException, IOException, ParseException {
		testStorage.loadFileWithDirectory(expectedDirectory, fileName2);
		File loadFile = new File(expectedDirectory + "/" + fileName2);

		ArrayList<String> stringList = new ArrayList<String>();
		String readLine;

		BufferedReader readFile = new BufferedReader(new FileReader(loadFile));
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}

		ArrayList<Task> mainListFromFile = testStorage.convertStringToTask(stringList, 0);

		testStorage.setMainList(mainListFromFile);
		testStorage.writeToFile();

		File defaultTextFile = new File(fileName1);
		FileAssert.assertEquals(loadFile, defaultTextFile);

		// loadFile.delete();

	}

	@Test
	public void saveAndLoadWithDirectory_NewFile_SaveAndLoadFile()
			throws NotDirectoryException, IOException, ParseException {
		System.out.println("Test with directory");
		saveToFileWithDirectory_NewFileAndDirectory_FileSaved();
		loadFileWithDirectory_NewFileAndDirectory_LoadNewFile();
	}

	@Test
	public void testLoadRecentFileWithDirectory_NewFile_LoadRecentFileWithDirectory()
			throws IOException, ParseException {
		testStorage.loadFileWithDirectory(expectedDirectory, fileName2);
		File loadFile = new File(expectedDirectory + "/" + fileName2);

		ArrayList<String> stringList = new ArrayList<String>();
		String readLine;

		BufferedReader readFile = new BufferedReader(new FileReader(loadFile));
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}

		ArrayList<Task> stringListToTask = testStorage.convertStringToTask(stringList, 0);

		int count = 0;
		for (int i = 0; i < recentList.size(); i++) {
			Task line1 = recentList.get(i);
			//System.out.println("line 1 is " + line1);
			Task line2 = stringListToTask.get(i);
			//System.out.println("line 2 is " + line2);

			if (line1.equals(line2)) {
				count++;
			}
		}
		// test content
		assertEquals(recentList.size(), count);
	}

	// ================================================================================
	// Extracted Methods
	// ================================================================================

	private static void printResult(ArrayList<Task> testArray) {
		for (int i = 0; i < testArray.size(); i++) {
			System.out.println(testArray.get(i).getDescription());
		}
	}

	public static void printString(ArrayList<Entry<String, Integer>> autoCompletionList) {

		Iterator<Entry<String, Integer>> iterator = autoCompletionList.iterator();
		while (iterator.hasNext()) {
			Entry<String, Integer> next = iterator.next();
			String entry = next.getKey();
			int freqCount = next.getValue();

			System.out.println("entry is " + entry + " and freqcount is " + freqCount);
		}
	}
}
