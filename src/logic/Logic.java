package logic;

import common.Command;
import common.Result;
import parser.Parser;

// need to import other classes *****
// command, result, parser, storage, etc.

// **all the switch cases should be in the parser class, then the the parser class can call the commands from logic class**

public class Logic{

	// Objects to call into other classes
	private Parser parser;
	
	public static void main(String[] args) {
		Logic logic = new Logic();
	}

	public Logic(){
		this.parser = new Parser();
	}
	
	public Result processCommand(String input) {
	    // parse input and retrieve the corresponding command class
		Command command = parser.parseCommand(input);
		// execute the command and return the result
		Result result = command.execute(command);
		return result;						
	}
	
}
