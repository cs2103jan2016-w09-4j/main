import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Storage {

	private static ArrayList<Task> mainList;
	private static ArrayList<Task> searchResults;
	private static String fileName;
	
	private static final String MESSAGE_IOEXCEPTION_ERROR = "IO Exception error";
	
	// default file name is "mytextfile.txt"
	public Storage(){
		mainList = new ArrayList<Task>();
		searchResults = new ArrayList<Task>();	
		fileName = "mytextfile.txt";
	}
	
	public Storage(String newFileName){
		mainList = new ArrayList<Task>();
		searchResults = new ArrayList<Task>();
		fileName = newFileName;
	}
	
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
		int taskIndex = taskID - 1;
		if (taskIndex >= mainList.size()) {
			return new ArrayList<Task>();
		} else if (taskIndex < 0) {
			return new ArrayList<Task>();
		} else {
			mainList.remove(taskIndex);
			writeToFile();
		}
		return mainList;
	}
	
	/*
	 * This method returns a empty arraylist if the taskID is invalid
	 * else, it returns the mainList arraylist
	 */
	public ArrayList<Task> editTask(int taskID, String newDescription) {
		int taskIndex = taskID - 1;
		if (taskIndex >= mainList.size()) {
			return new ArrayList<Task>();
		} else if (taskIndex < 0) {
			return new ArrayList<Task>();
		} else {
			Task getTask = mainList.get(taskIndex);
			getTask.setDescription(newDescription);
			writeToFile();
		}
		return mainList;
	}
	
	/*
	 * This method returns a empty arraylist if there is no such keyword
	 * else, it returns the searchResults arraylist 
	 */
	public ArrayList<Task> searchTask(String keyword) {
		for (int i=0; i<mainList.size(); i++) {
			if (mainList.get(i).getDescription().contains(keyword)) {
				searchResults.add(mainList.get(i));				
			}
		}
		
		return searchResults;
	}
	
	public ArrayList<Task> getMainList() {
		return mainList;
	}
}
