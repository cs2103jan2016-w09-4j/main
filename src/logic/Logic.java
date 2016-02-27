package logic;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Collections;

import storage.Storage;
import logic.TaskList;

// need to import other classes *****
// command, result, parser, storage, etc.

// **all the switch cases should be in the parser class, then the the parser class can call the commands from logic class**


public class Logic{
	
	// Objects to call into other classes
	private Storage storage;
	private Parser parser;
	
	private String fileName;
		
	private final ArrayList<String> textList = new ArrayList<String>();

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args){
	
		Logic logic = new Logic();
	}

	public Logic(){

		this.storage = new Storage();
		this.parser = new Parser();
	}
	
	

	/* logic checks whether if there is a file in the directory that has the same name as the user input.
		If there is then this method will read in the contents of that existing file and add the contents into
		the program's own textList. Any changes made in the textList thereafter will be saved into the existing
		file as well.
	*/
	
	public Result processCommand(String input)
	{
		
		Command command = this.parser.parseCommand(input);    // parse the input, and returns the correct command class to go into
		
		Result result = command.execute();  // goes into whatever command it is and execute and return a result
											// need to create a Result class for this architecture
		return result;
	}
	
	

}

