//@@author A0127257A
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map.Entry;

import junitx.framework.FileAssert;

import org.junit.Before;
import org.junit.Test;

import gridtask.common.Task;
import gridtask.storage.Storage;

public class StorageTest {

	private Storage testStorage;
	private ArrayList<Task> mainList;
	private ArrayList<Task> getDataFromFile;
	private ArrayList<Task> completedList;
	private ArrayList<Entry<String, Integer>> autocompletionList;
	private String fileName1 = "MyTasks.txt";
	private String testfile = "StorageTestFile.txt";
	private String autoCompletionTextFile = "AutoCompletion.txt";
	private String defaultFileName = "DefaultFile.txt";

	private String outputException = "";
	Path currentRelativePath = Paths.get("");
	String expectedDirectory = currentRelativePath.toAbsolutePath().toString();

	@Before
	public void init() throws IOException, ParseException {
		testStorage = new Storage();
		mainList = testStorage.getMainList();
		completedList = testStorage.getDoneList();
		autocompletionList = testStorage.getAutoCompletionList();
		getDataFromFile = new ArrayList<Task>();

		if (mainList.isEmpty()) {
			System.out.println("Main list is empty");
		} else {
			mainList.clear();
		}

		if (autocompletionList.isEmpty()) {
			System.out.println("Autocompletion list is empty");
		} else {
			autocompletionList.clear();
		}

		if (completedList.isEmpty()) {
			System.out.println("CompletedList is empty");
		} else {
			completedList.clear();
		}

		// add task into mainList
		// first task have all 3 fields
		Task task1 = new Task("meeting at night");
		task1.setStart("22/03/2016 13:00");
		task1.setEnd("22/03/2016 16:00");
		ArrayList<String> categoryToAdd = new ArrayList<String>();
		categoryToAdd.add("work");
		categoryToAdd.add("school");
		task1.setCategories(categoryToAdd);

		// have 2 fields
		Task task2 = new Task("gathering");
		task2.setEnd("25/03/2016 14:00");
		task2.setImportance(true);

		mainList.add(task1);
		mainList.add(task2);

		// adding task into completed list
		Task task3 = new Task("do tutorial");
		ArrayList<String> categoryToAdd2 = new ArrayList<String>();
		categoryToAdd2.add("homework");
		task3.setCategories(categoryToAdd2);

		// have 1 field
		Task task4 = new Task("meet friends");
		task4.setImportance(true);

		completedList.add(task3);
		completedList.add(task4);

		// add string as autocompletion
		String line1 = "add meeting now";
		String line2 = "gathering at 4pm";
		autocompletionList.add(new AbstractMap.SimpleEntry<String, Integer>(line1, 2));
		autocompletionList.add(new AbstractMap.SimpleEntry<String, Integer>(line2, 2));
	}

	@Test
	public void writeToFile_ContentsOfAnotherFile_CorrectNumOfTasksAndContent() throws IOException {
		String readLine;
		testStorage.writeToFile();

		// read file into arraylist
		File defaultTextFile = new File(fileName1);
		BufferedReader readFile = new BufferedReader(new FileReader(defaultTextFile));

		ArrayList<String> stringList = new ArrayList<String>();
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}
		
		File testFile = new File("testing/expected_writeToFile_AllTaskTypes");
		FileAssert.assertEquals(defaultTextFile, testFile);
	}

	@Test
	public void writeAutoCompletionData_ContentsOfAnotherFile_CorrectNumOfStringandContent() throws IOException {
		testStorage.writeAutoCompletionData();

		String readLine;

		// read file into arraylist
		File defaultTextFile = new File(autoCompletionTextFile);
		BufferedReader readFile = new BufferedReader(new FileReader(defaultTextFile));

		ArrayList<String> stringList = new ArrayList<String>();
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}

		assertEquals(autocompletionList.size(), stringList.size());

		int count = 0;
		for (int i = 0; i < stringList.size(); i++) {
			String line1 = stringList.get(i);
			String line2 = autocompletionList.get(i).getKey() + " : " + "Frequency : "
					+ autocompletionList.get(i).getValue();

			if (line1.equals(line2)) {
				count++;
			}
		}
		// test content
		assertEquals(autocompletionList.size(), count);
	}

	@Test
	public void saveToFileNoDirectory_NewFile_FileSaved() throws FileNotFoundException, IOException, ParseException {

		testStorage.saveToFile(testfile);
		File defaultFile = new File(fileName1);
		File newFile = new File(testfile);
		FileAssert.assertEquals(defaultFile, newFile);

		newFile.delete();
	}

	@Test
	public void loadFileWithFileName_DefaultTextFile_LoadListFromFile()
			throws FileNotFoundException, IOException, ParseException {

		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, mainList);
	}

	@Test
	public void loadFile_InvalidFileName_ExceptionThrown() throws IOException, ParseException {

		testStorage.saveToFile(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, mainList);
		
		try {
			testStorage.loadFileWithFileName("not a file");
		} catch (FileNotFoundException fnfe) {
			outputException = "Invalid File";
		}

		assertEquals("Invalid File", outputException);
	}

	@Test
	public void saveToFileWithDirectory_NewFileAndDirectory_FileSaved() throws NotDirectoryException, IOException {
		testStorage.saveToFileWithDirectory(expectedDirectory, testfile);
		
		// Read DefaultFile.txt to see if it is written correctly
		File defaultTextFile = new File(defaultFileName);
		Scanner scanner = new Scanner(defaultTextFile);
		String getRecentLine = scanner.nextLine();
		scanner.close();

		String expectedLine = expectedDirectory + " , " + testfile;

		assertEquals(expectedLine, getRecentLine);

		File newFile = new File(expectedDirectory + "/" + testfile);
		File defaultFile = new File(fileName1);
		FileAssert.assertEquals(defaultFile, newFile);

	}

	public void loadFileWithDirectory_NewFileAndDirectory_LoadNewFile()
			throws NotDirectoryException, FileNotFoundException, IOException, ParseException {
		testStorage.loadFileWithDirectory(expectedDirectory, testfile);
		File loadFile = new File(expectedDirectory + "/" + testfile);

		ArrayList<String> stringList = new ArrayList<String>();
		String readLine;

		BufferedReader readFile = new BufferedReader(new FileReader(loadFile));
		while ((readLine = readFile.readLine()) != null) {
			stringList.add(readLine);
		}
		
		File defaultTextFile = new File(fileName1);
		FileAssert.assertEquals(defaultTextFile, loadFile);
	}

	public void saveAndLoadWithDirectory_NewFile_SaveAndLoadFile()
			throws NotDirectoryException, IOException, ParseException {
		saveToFileWithDirectory_NewFileAndDirectory_FileSaved();
		loadFileWithDirectory_NewFileAndDirectory_LoadNewFile();
	}
	
	@Test
	public void getAutoCompletionList_AutoCompletionFileDeleted_ReturnEmptyArrayList() {
		File autoCompletionFile = new File(autoCompletionTextFile);
		autoCompletionFile.delete();
		
		ArrayList<Entry<String, Integer>> newAutoCompletionList = testStorage.getAutoCompletionList();
		ArrayList<Entry<String, Integer>> emptyList = new ArrayList<Entry<String, Integer>>();
		
		assertEquals(emptyList,newAutoCompletionList);
		
	}
}
