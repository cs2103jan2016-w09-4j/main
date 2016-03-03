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
<<<<<<< HEAD
	
	public Result processCommand(String input) {
		Command command = parser.parseCommand(input);    // parse the input, and returns the correct command class to go into
		Result result = command.execute(command, storage);  // goes into whatever command it is and execute and return a result										// after getting back the command then how? give it to command class to allocate to the 
		return result;						// correct class inside the Command Class?
=======

	public Result processCommand(String input)
	{
	    System.out.println(input);
		Command command = parser.parseCommand(input);
		Result result = new Result(true, "SUCCESS!", command.execute(storage));

		ArrayList<Task> tasks = result.getResults();

		System.out.println("-----");
		for (Task t:tasks){
		    System.out.println(t.getID() + " " + t.getDescription());
		}

		return result;
>>>>>>> try_stuff
	}

}
