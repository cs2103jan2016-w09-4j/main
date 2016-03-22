import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junitx.framework.FileAssert;
import org.junit.Test;

import common.Task;
import storage.Storage;

public class StorageTest {
	
	private Storage testStorage;
	private ArrayList<Task> testList;
	private ArrayList<Task> getDataFromFile;
	private Task taskToAdd;
	private String fileName1 = "mytextfile.txt";
	private String fileName2 = "newFile";

	private String outputException = "";
	private String outputException2 = "";
	private String outputException3 = "";
	String expectedDirectory = "/Users/Documents/testDirectory";

	@Test
	public void testMethods() throws IOException {
		System.out.println("initializing");
		testStorage = new Storage();
		testList = testStorage.getMainList();
		getDataFromFile = new ArrayList<Task>();

		String readLine;
		Task taskFromFile;
		int count;
		File file1, file2, file3;

		if (testList.isEmpty()) {
			System.out.println("main list is empty");
		} else {
			System.out.println("Printing main list");
			printResult(testList);
			System.out.println("Clear mainlist");
			testList.clear();
		}

		// Add task into testList
		for (int i = 1; i < 3; i++) {
			taskToAdd = new Task(String.valueOf(i));
			testList.add(taskToAdd);
		}

		System.out.println("Print main list");
		printResult(testList);

		// @Test writeToFile
		testStorage.writeToFile();

		// read file into arraylist
		File defaultTextFile = new File(fileName1);
		BufferedReader readFile = new BufferedReader(new FileReader(defaultTextFile));

		while ((readLine = readFile.readLine()) != null) {
			taskFromFile = new Task(readLine);
			getDataFromFile.add(taskFromFile);
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

		// @Test saveToFile
		getDataFromFile.clear();
		testStorage.saveToFile(fileName2);
		file2 = new File(fileName1);
		file3 = new File(fileName2);
		FileAssert.assertEquals(file2, file3);

		// file2.delete();
		file3.delete();

		// @Test loadFileWithFileName()
		getDataFromFile.clear();
		file1 = new File(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, testList);

		file1.delete();

		// @Test saving and loading
		getDataFromFile.clear();
		testStorage.saveToFile(fileName1);
		getDataFromFile = testStorage.loadFileWithFileName(fileName1);
		assertEquals(getDataFromFile, testList);

		// @Test testInvalidFileForLoading()
		try {
			testStorage.loadFileWithFileName("not a file");
		} catch (FileNotFoundException fnfe) {
			outputException3 = "Invalid File";
		}

		assertEquals("Invalid File", outputException3);
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
}