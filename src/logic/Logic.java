package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import java.util.ArrayList;

public class Logic {

	// Objects to call into other classes
	private Parser parser;
	private Storage storage;
	
	private static final String MESSAGE_NOT_SAVED = "Error: File not saved!\n";
	
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
					storage.saveToFile(description);
				} catch (Exception e){
					System.out.println(MESSAGE_NOT_SAVED);
				}
				break;
				
			case LOAD:
				list = storage.loadFileWithFileName(description);
				break;
				
			case UNDO:
				
				break;
				
			case REDO: 
			
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
