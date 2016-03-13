package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import logic.Execution;

import java.util.ArrayList;
import java.util.HashMap;

public class Logic {

	// Objects to call into other classes
	private Parser parser;
	@SuppressWarnings("unused")
	private Storage storage;
	private Execution execution;
	private static Logic logic = new Logic();
	
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
				
			case COMPLETE:
				list = execution.completeCommand(taskID);
				break;
				
			case DONE:
				list = execution.getDoneList();
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
	
	public ArrayList<Task> getMainList(){
		return execution.getMainList();
	}
	
	public static Logic getInstance(){
		
		if (logic == null){
			return logic = new Logic();
		}
		
		return logic;
	}

    public HashMap<String, Integer> getCategories() {
        // TODO : placeholder for categories
        HashMap<String, Integer> cat = new HashMap<String, Integer>();
        cat.put("Priority", 1);
        cat.put("Today", 2);
        return cat;
    }
}	
