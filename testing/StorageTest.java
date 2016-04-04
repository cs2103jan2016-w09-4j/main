import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import junitx.framework.FileAssert;

import org.junit.Before;
import org.junit.Test;

import common.Task;
import storage.Storage;

public class StorageTest {

	private Storage testStorage;
	private ArrayList<Task> testList;
	private ArrayList<Task> getDataFromFile;
	private ArrayList<String> autocompletionList;
	private String fileName1 = "MyTasks.txt";
	private String fileName2 = "newFile";
	private String fileName3 = "AutoCompletion.txt";

	private String outputException = "";
	String expectedDirectory = "/Users/Documents/testDirectory";

	@Before
	public void init() throws IOException {
		System.out.println("initializing");
		testStorage = new Storage();
		testList = testStorage.getMainList();
		autocompletionList = testStorage.getAutoCompletionList();
		getDataFromFile = new ArrayList<Task>();

		if (testList.isEmpty()) {
			System.out.println("main list is empty");
		} else {
			System.out.println("Printing main list");
			printResult(testList);
			System.out.println("Clear mainlist");
			testList.clear();
		}
		
		if (autocompletionList .isEmpty()) {
			System.out.println("autocompletion list is empty");
		} else {
			System.out.println("Printing autocompletion list");
			printString(autocompletionList);
			System.out.println("Clear autocompletion list");
			autocompletionList.clear();
		}

		// Add task into testList
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
		
		Task task3 = new Task("do tutorial");
		ArrayList<String> categoryToAdd2 = new ArrayList<String>();
		categoryToAdd2.add("homework");
		task3.setCategories(categoryToAdd2);
		
		// have 1 field
		Task task4 = new Task("meet friends");

		testList.add(task1);
		testList.add(task2);
		testList.add(task3);
		testList.add(task4);
		
		//add string as autocompletion
		String line1 = "add meeting now";
		String line2 = "gathering at 4pm";
		autocompletionList.add(line1);
		autocompletionList.add(line2);

		System.out.println("Print main list");
		printResult(testList);
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
			e.printStackTrace();
		}

		assertEquals(testList.size(), getDataFromFile.size());

		count = 0;
		for (int i = 0; i < testList.size(); i++) {
			Task task1 = testList.get(i);
			Task task2 = getDataFromFile.get(i);

			if (task1.getDescription().equals(task2.getDescription())) {
				count++;
			}
		}
		// test content
		assertEquals(testList.size(), count);
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

		System.out.println("size of autocompletionList is " + autocompletionList.size());
		System.out.println("size of stringList is " + stringList.size());

		assertEquals(autocompletionList.size(), stringList.size());

		int count = 0;
		for (int i = 0; i < stringList.size(); i++) {
			String line1 = stringList.get(i);
			String line2 = autocompletionList.get(i);

			if (line1.equals(line2)) {
				count++;
			}
		}
		// test content
		assertEquals(autocompletionList.size(), count);
	}

	@Test
	public void saveToFile_NewFile_FileSaved() throws FileNotFoundException, IOException, ParseException {

		getDataFromFile.clear();
		testStorage.saveToFile(fileName2);
		File file2 = new File(fileName1);
		File file3 = new File(fileName2);
		FileAssert.assertEquals(file2, file3);

		// file2.delete();
		file3.delete();

		// @Test loadFileWithFileName()
		getDataFromFile.clear();
		File file1 = new File(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, testList);

		file1.delete();
	}

	@Test
	public void loadFile_InvalidFileName_ExceptionThrown() throws IOException, ParseException {

		getDataFromFile.clear();
		testStorage.saveToFile(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, testList);

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


	/*
	 * @Test public void appendToFile() throws IOException { File file1 = new
	 * File("mytextfile.txt"); testStorage.writeToFile(); taskToAdd = new Task(
	 * "new task"); testStorage.appendToFile(taskToAdd); testList.add(new Task(
	 * "new task"));
	 * 
	 * BufferedReader readFile = new BufferedReader(new FileReader(file1));
	 * String readLine; ArrayList<Task> lineFromFile = new ArrayList<Task>();
	 * while((readLine = readFile.readLine()) != null) { Task taskFromFile = new
	 * Task(readLine); lineFromFile.add(taskFromFile); }
	 * assertEquals(testList.size(),lineFromFile.size()); testList.remove(5);
	 * readFile.close(); }
	 * 
	 * 
	 * @Test public void testSaveAndLoad() throws IOException, ParseException {
	 * testStorage.saveToFile("newTextFile"); getDataFromFile =
	 * testStorage.loadFileWithFileName("newTextFile");
	 * assertEquals(getDataFromFile, testList); }
	 * 
	 * 
	 * @Test public void testInvalidDirectoryForLoading() throws
	 * FileNotFoundException, ParseException { try{
	 * testStorage.loadFileWithDirectory("directory", "userFileName"); } catch
	 * (NotDirectoryException exp) { outputException2 = "Invalid Directory"; }
	 * assertEquals("Invalid Directory",outputException2); }
	 * 
	 */

	// ================================================================================
	// Extracted Methods
	// ================================================================================

	private static void printResult(ArrayList<Task> testArray) {
		for (int i = 0; i < testArray.size(); i++) {
			System.out.println(testArray.get(i).getDescription());
		}
	}
	
	private static void printString(ArrayList<String> testArray) {
		for (int i = 0; i < testArray.size(); i++) {
			System.out.println(testArray.get(i));
		}
	}
}
