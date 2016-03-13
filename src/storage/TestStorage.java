package storage;

import static org.junit.Assert.*;
import junitx.framework.FileAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import common.Task;

public class TestStorage {
	
	private Storage testStorage;
	private ArrayList<Task> testList;
	private ArrayList<Task> getDataFromFile;
	private Task taskToAdd;
	private String outputException = "";
	private String outputException2 = "";
	private String outputException3 = "";
	String expectedDirectory = "/Users/Documents/testDirectory";
	
	@Before
	public void init() {
		testStorage = new Storage();
		// add task to testList as content for testing
		testList = testStorage.getMainList();
		for (int i = 1; i < 6; i++) {
			taskToAdd = new Task(String.valueOf(i));
			testList.add(taskToAdd);
		}
	}
	
	@Test 
	public void testWriteToFile() throws IOException {
		File file1 = new File("mytextfile.txt");
		testStorage.writeToFile();
		BufferedReader readFile = new BufferedReader(new FileReader(file1));
		String readLine;
		ArrayList<Task> lineFromFile = new ArrayList<Task>();
		while((readLine = readFile.readLine()) != null) {
			Task taskFromFile = new Task(readLine);
			lineFromFile.add(taskFromFile);
		}
		assertEquals(testList.size(),lineFromFile.size());
	}
	
	@Test 
	public void appendToFile() throws IOException {
		File file1 = new File("mytextfile.txt");
		testStorage.writeToFile();
		taskToAdd = new Task("new task");
		testStorage.appendToFile(taskToAdd);
		testList.add(new Task("new task"));
		
		BufferedReader readFile = new BufferedReader(new FileReader(file1));
		String readLine;
		ArrayList<Task> lineFromFile = new ArrayList<Task>();
		while((readLine = readFile.readLine()) != null) {
			Task taskFromFile = new Task(readLine);
			lineFromFile.add(taskFromFile);
		}
		assertEquals(testList.size(),lineFromFile.size());
		testList.remove(5);
	}
	
	@Test
	public void testSaveToFile() throws IOException {
		File file2 = new File("mytextfile.txt");
		testStorage.writeToFile();
		File file3 = new File("testFile");
		testStorage.saveToFile("testFile");
		FileAssert.assertEquals(file2,file3);
		
		file2.delete();
		file3.delete();
		
	}
	
	@Test
	public void testLoadFileWithFileName() throws ParseException, IOException {
		File file1 = new File("mytextfile.txt");
		testStorage.writeToFile();
		getDataFromFile = testStorage.loadFileWithFileName("mytextfile.txt");
		assertEquals(getDataFromFile, testList);
	}
	
	@Test
	public void testSaveAndLoad() throws IOException, ParseException {
		testStorage.saveToFile("newTextFile");
		getDataFromFile = testStorage.loadFileWithFileName("newTextFile");
		assertEquals(getDataFromFile, testList);
	}
	
	@Test
	public void testInvalidDirectoryForSaving() throws IOException {
		try{
			testStorage.saveToFileWithDirectory("invalid", "newFile");
		} catch (NotDirectoryException e) {
			outputException = "Invalid Directory"; 
		}
		assertEquals("Invalid Directory",outputException);
		
	}
	
	@Test
	public void testInvalidDirectoryForLoading() throws FileNotFoundException, ParseException {
		try{
			testStorage.loadFileWithDirectory("directory", "userFileName");
		} catch (NotDirectoryException exp) {
			outputException2 = "Invalid Directory"; 
		}
		assertEquals("Invalid Directory",outputException2);
	}
	
	@Test 
	public void testInvalidFileForLoading() throws NotDirectoryException, ParseException {
		try{
			testStorage.loadFileWithFileName("not a file");
		} catch (FileNotFoundException fnfe) {
			outputException3 = "Invalid File"; 
		}
		
		assertEquals("Invalid File",outputException3);
	}
}
