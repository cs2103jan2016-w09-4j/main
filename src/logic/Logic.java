package logic;

import common.Result;
import common.Command;

import parser.Parser;
import storage.Storage;

// need to import other classes *****
// command, result, parser, storage, etc.

// **all the switch cases should be in the parser class, then the the parser class can call the commands from logic class**

public class Logic{
	
	// Objects to call into other classes
	private Parser parser;
	private Storage storage;

	public Logic(){
		this.parser = new Parser();
		this.storage = new Storage();
	}
	
	public Result processCommand(String input) {
		Command command = parser.parseCommand(input);    // parse the input, and returns the correct command class to go into
		Result result = command.execute(command, storage);  // goes into whatever command it is and execute and return a result										// after getting back the command then how? give it to command class to allocate to the 
		return result;						// correct class inside the Command Class?
	}
	
}
