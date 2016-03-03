package logic;

import common.*;

import parser.Parser;
import storage.Storage;
import java.util.ArrayList;

public class Logic {

	// Objects to call into other classes
	private Parser parser;
	private Storage storage;

	public Logic() {
		this.parser = new Parser();
		this.storage = new Storage();
	}

	private ArrayList<Task> execute(Command command){
	    //Hey Gilbert, do this!!
	    return null;
	}

	public Result processCommand(String input) {
	    System.out.println(input);
		Command command = parser.parseCommand(input);
		Result result = new Result(true, "SUCCESS!", execute(command));

		ArrayList<Task> tasks = result.getResults();

		System.out.println("-----");
		for (Task t:tasks){
		    System.out.println(t.getID() + " " + t.getDescription());
		}

		return result;
	}

}
