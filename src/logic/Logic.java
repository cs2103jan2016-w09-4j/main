package logic;

import common.*;

import parser.Parser;
import storage.Storage;
import java.util.ArrayList;

public class Logic{

	// Objects to call into other classes
	private Parser parser;
	private Storage storage;

	public Logic(){
		this.parser = new Parser();
		this.storage = new Storage();
	}

	public ArrayList<Task> execute(Command command){
	    return new ArrayList<Task>();  // dummy
	}

	public ArrayList<Task> execute(AddCommand command){
	    return storage.addTask(command.getDescription());
	}

	public ArrayList<Task> execute(DeleteCommand command){
	    return storage.deleteTask(command.getId());
	}

    public ArrayList<Task> execute(EditCommand command){
        return storage.editTask(command.getId(), command.getDescription());
    }

	public ArrayList<Task> execute(SearchCommand command){
	    return storage.searchTask(command.getKeyword());
	}

	public Result processCommand(String input)
	{

		Command command = parser.parseCommand(input);
		Result result = new Result(true, "SUCCESS!", execute(command));
		//debug
		ArrayList<Task> tasks = result.getResults();
		for (Task t:tasks){
		    System.out.println(t.getID() + " " + t.getDescription());
		}
		//

		return result;
	}

}
