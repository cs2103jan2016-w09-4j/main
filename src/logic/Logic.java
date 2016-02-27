package logic;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Collections;

// need to import other classes *****
// command, result, parser, storage, etc.

// **all the switch cases should be in the parser class, then the the parser class can call the commands from logic class**


public class Logic{
	
	private static final int INDEX_ZERO = 0;
	private static final int INDEX_ONE = 1;
	
	private static final String MESSAGE_USAGE = "Usage of logic: java logic [Your File Name]\n";
	private static final String MESSAGE_WELCOME = "Welcome to logic. %s is ready for use\n";
	private static final String MESSAGE_COMMAND = "command: ";
	private static final String MESSAGE_ADD = "added to %s: \"%s\"\n"; 
	private static final String MESSAGE_CLEAR = "all content deleted from %s\n"; 	
	private static final String MESSAGE_DELETE = "deleted from %s: \"%s\"\n";
	private static final String MESSAGE_INVALID_COMMAND = "invalid command!\n";
	private static final String MESSAGE_READING_ERROR = "Error: unable to read from %s\n";
	private static final String MESSAGE_NOT_SAVED = "Error: unable to save into file\n";
	private static final String MESSAGE_FILE_EMPTY = "%s is empty\n";
	private static final String MESSAGE_INDEX_ERROR = "Error: invalid index\n";
	private static final String MESSAGE_DISPLAY = "%d. %s\n";
	private static final String MESSAGE_SORT = "all texts have been sorted alphabetically\n";
	private static final String MESSAGE_NOT_SORTED = "Error: unable to sort!\n";
	private static final String MESSAGE_DOES_NOT_CONTAIN = "textList does not contain the keyword %s\n";
	
	
	// To indicate whether file was successfully saved or not
	private static final boolean IS_FILE_SAVED = true;
	private static final boolean IS_FILE_NOT_SAVED = false;


	enum COMMANDS{
		DISPLAY, ADD, DELETE, CLEAR, SEARCH, SORT, EXIT
	};	

	// Objects to call into other classes
	private Storage storage;
	private Parser parser;
	
	private String fileName;
		
	private final ArrayList<String> textList = new ArrayList<String>();

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args){
	
		Logic logic = new Logic();
		logic.runLogic();
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
		
		Result result = command.execute();  // goes into whatever command it is and execute
		
		return result;
	}
	
		

}

