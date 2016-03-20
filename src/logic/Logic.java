package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import logic.Execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


public class Logic {

	// Objects to call into other classes
	private Parser parser;
	private Storage storage;
	private Execution execution;
	private static Logic logic = new Logic();
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger("logic");
	
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
				System.out.println(getPredictions());
				break;
			
			case DELETE:
				list = execution.deleteTask(taskID);
				break;
			
			case EDIT:
				list = execution.editTask(taskID, description);
				System.out.println(getPredictions());
				break;
				
			case SEARCH:
				list = execution.searchTask(description);
				System.out.println(getPredictions());
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
				list = execution.completeTask(taskID);
				break;
				
			case DONE:
				list = execution.getDoneList();
				break;
				
			case EXIT:
				execution.exitCommand();
				
			case INVALID:
				list = null;
				break;
				
			default:
				list = null;
				
		}
		
		assert(list != null);
		Collections.sort(list);
	    return list;
	}

	public Result processCommand(String input) {
	    System.out.println(input);
	    
		Command command = parser.parseCommand(input);
		assert(command != null);
		
		Result result = new Result(command.getType(), true, execute(command));
		assert(result != null);

		return result;
	}
	
	public ArrayList<String> getPredictions(){
		return execution.getDictionary();
	}
	
	public ArrayList<String> getPredictions(String input) {
	    if (input.isEmpty()) {
	        return null;
	    } else {
	        return getPredictions();
	    }
	}
	
	public ArrayList<Task> getMainList(){
		assert(storage.getMainList() != null);
		return storage.getMainList();
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
