package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Logic {

	// Objects to call into other classes
	private Parser parser;
	private Storage storage;
	
	private static final String MESSAGE_NOT_SAVED = "Error: File not saved!\n";
	private static final String MESSAGE_INVALID_FILE = "Error: File not found!\n";
	
	public Logic() {
		this.parser = new Parser();
		this.storage = new Storage();
	}

	private ArrayList<Task> execute(Command command){
	    
		ArrayList<Task> list = new ArrayList<Task>();
		
		CommandType commandType = command.getType();
		String description = command.getDescription();
		int taskID = command.getId();		
		
		switch(commandType){
		
			case ADD:
				list = storage.addTask(description); 
				break;
			
			case DELETE:
				list = storage.deleteTask(taskID);
				break;
			
			case EDIT:
				list = storage.editTask(taskID, description);
				break;
				
			case SEARCH:
				list = storage.searchTask(description);
				break;
			
			case HOME:
				list = storage.getMainList();
				break;
				
			case SAVE:
				try{	
					if(description.contains(" ")){
						String[] split = description.split(" ");
						String directory = split[0].toLowerCase();
						String userFileName = split[1];
						storage.saveToFileWithDirectory(directory, userFileName);
					} else{
						storage.saveToFile(description);
					}	
				} catch (Exception e){
					System.out.println(MESSAGE_NOT_SAVED);
				}
				list = storage.getMainList();
				break;
				
			case LOAD:
				
				try {
					if(description.contains(" ")){
						String[] split = description.split(" ");
						String directory = split[0].toLowerCase();
						String userFileName = split[1];
						storage.loadFileWithDirectory(directory, userFileName);
					} else{					
						storage.loadFileWithFileName(description);
					}	
				} catch (FileNotFoundException e) {
					System.out.println(MESSAGE_INVALID_FILE);
				}
				
				list = storage.getMainList();
				break;
				
			case UNDO:
				list = storage.undoCommand();
				break;
				
			case REDO: 
				list = storage.redoCommand();
				break;
				
			case INVALID:
				list = null;
				break;
				
			default:
				list = null;
				
		}
	    return list;
	}

	public Result processCommand(String input) {
	    System.out.println(input);
		Command command = parser.parseCommand(input);
		Result result = new Result(command.getType(), true, execute(command));

		ArrayList<Task> tasks = result.getResults();
		System.out.println("-----");
		for (Task t:tasks){
		    System.out.println(t.getID() + " " + t.getDescription());
		}

		return result;
	}
}	
