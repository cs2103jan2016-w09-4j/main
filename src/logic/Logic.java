package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import logic.Execution;

import java.util.ArrayList;

public class Logic {

	// Objects to call into other classes
	private Parser parser;
	@SuppressWarnings("unused")
	private Storage storage;
	private Execution execution;
	
	public Logic() {
		this.parser = new Parser();
		this.storage = new Storage();
		this.execution = new Execution();
	}

	private ArrayList<Task> execute(Command command){
	    
		ArrayList<Task> list = new ArrayList<Task>();
		
		CommandType commandType = command.getType();
		String description = command.getDescription();
		int taskID = command.getId();		
		
		switch(commandType){
		
			case ADD:
				list = execution.addTask(description); 
				break;
			
			case DELETE:
				list = execution.deleteTask(taskID);
				break;
			
			case EDIT:
				list = execution.editTask(taskID, description);
				break;
				
			case SEARCH:
				list = execution.searchTask(description);
				break;
			
			case HOME:
				list = execution.getMainList();
				break;
				
			case SAVE:
				execution.savingTasks(description);
				list = execution.getMainList();
				break;
				
			case LOAD:
				list = execution.loadingTasks(description);
				break;
				
			case UNDO:
				list = execution.undoCommand();
				break;
				
			case REDO: 
				list = execution.redoCommand();
				break;
				
			case DONE:
				list = execution.doneCommand(taskID);
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

		return result;
	}
	
	public ArrayList<String> getPredictions(){
		return execution.getDictionary();
	}
}	
