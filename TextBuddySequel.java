
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Collections;

/**
	Author: 
		Gilbert Chua
		A0123972
		Tutorial ID W09

	TextBuddySequel is a program that can modify texts. Users can either create a new file
	or use an existing file by inputting a file name in the command line parameter, and the program can check 
	if such a file exists. If it exists then the program will use its contents, otherwise it will create a 
	new file under the specified name. TextBuddySequel can add, delete, display, clear contents into and from the
	file. The file will be automatically saved when there are changes made to the file. Keying in "exit" will
	terminate the program. Below is an example of how TextBuddySequel is used:
	
	*NEW FEATURE* 
	*	Users can now sort the list in alphabetical order, simply by inputting "sort" in the command line parameter.
	*	Users can also search for a specific keyword in this format: "search <keyword>".

		c:> TextBuddySequel.exe mytextfile.txt(OR c:> java TextBudy mytextfile.txt)
		Welcome to TextBuddySequel. mytextfile.txt is ready for use
		command: add little brown fox
		added to mytextfile.txt: "little brown fox"
		command: display
		1. little brown fox
		command: add jumped over the moon
		added to mytextfile.txt: "jumped over the moon"
		command: display
		1. little brown fox
		2. jumped over the moon
		command: delete 2
		deleted from mytextfile.txt: "jumped over the moon"
		command: display
		1. little brown fox
		command: clear
		all content deleted from mytextfile.txt
		command: display
		mytextfile.txt is empty
		command: exit

*/


public class TextBuddySequel{
	
	private static final int INDEX_ZERO = 0;
	private static final int INDEX_ONE = 1;
	
	private static final String MESSAGE_USAGE = "Usage of TextBuddySequel: java TextBuddySequel [Your File Name]\n";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddySequel. %s is ready for use\n";
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
	private static final String MESSAGE_ERROR_SEARCHING = "Error: unable to search!\n";
	
	// To indicate whether file was successfully saved or not
	private static final boolean IS_FILE_SAVED = true;
	private static final boolean IS_FILE_NOT_SAVED = false;


	enum COMMANDS{
		DISPLAY, ADD, DELETE, CLEAR, SEARCH, SORT, EXIT
	};	

	private String fileName;
	
	private final ArrayList<String> textList = new ArrayList<String>();

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args){
	
		TextBuddySequel textBuddySequel = new TextBuddySequel(args);
		textBuddySequel.runTextBuddySequel();
	}

	public TextBuddySequel(String[] args){

		if (args.length > 0) {
			fileName = args[0];		
		}  else {
			showToUser(MESSAGE_USAGE);
			System.exit(0);
		}
		
		checkExistingFile(); 
		welcomeMessage(fileName);
	}
	
	public TextBuddySequel(){
	
		fileName = "textFile.txt";
		welcomeMessage(fileName);
	}	
	

	/* TextBuddySequel checks whether if there is a file in the directory that has the same name as the user input.
		If there is then this method will read in the contents of that existing file and add the contents into
		the program's own textList. Any changes made in the textList thereafter will be saved into the existing
		file as well.
	*/
	private void checkExistingFile(){
		try{
			File infile = new File(fileName);
			if(infile.exists()){
				String line;
				BufferedReader br = new BufferedReader(new FileReader(fileName));
				while ((line = br.readLine()) != null){
					textList.add(line);
				}
				br.close();
			}
		} catch (Exception e){
			textList.clear();
			showToUser(String.format(MESSAGE_READING_ERROR, fileName));
		}
	}	
				
	// The main operation on TextBuddySequel. It handles the add, delete, display, clear and exit commands
	public void runTextBuddySequel(){
	
		String output = "";

		while(true){
			showToUser(MESSAGE_COMMAND);
			try{
				output = executeRun(in.readLine());
			}	catch (Exception e) { 
					output = MESSAGE_INVALID_COMMAND;
			}
				showToUser(output);
				
		}	
	}	
	
	public String executeRun(String input){
		String output = "";
		String[] command = input.trim().split(" ", 2);
		
		switch(COMMANDS.valueOf(command[INDEX_ZERO].toUpperCase())){

			case ADD :
				output = addToList(command[INDEX_ONE]); 
				break;

			case DELETE :
				output = deleteFromList(command[INDEX_ONE]);
				break;

			case DISPLAY :
				output = displayList(textList);
				break;

			case CLEAR :
				output = clearList();
				break;
				
			case SEARCH :
				output = searchList(command[INDEX_ONE]);
				break;
				
			case SORT : 
				output = sortList();
				break;
				
			case EXIT :
				System.exit(0);

			default:
				output = MESSAGE_INVALID_COMMAND;
		}
		
		return output;
	
	}
		
	// Displays the welcome message to user
	private void welcomeMessage(String fileName){

		System.out.print(String.format(MESSAGE_WELCOME, fileName));

	}	

	private void showToUser(String text){
		System.out.print(text);
	}	

	// Adds the user input into the textList, and saving the addition into the file
	private String addToList(String text){
		textList.add(text);
		
		boolean isFileSaved = saveToFile();

		if (isFileSaved) {
			return String.format(MESSAGE_ADD, fileName, text);
		} else { 
			return String.format(MESSAGE_NOT_SAVED);
		}
	}

	// Write any changes to the file after any changes made to the textList itself
	private boolean saveToFile(){
		
		try{
			FileWriter file = new FileWriter(fileName);
			String text = "";
			
			for (String st : textList){
				text = text + st + "\n";
			}

			file.write(text);
			file.flush();
			file.close();
		} catch (IOException e){
			return IS_FILE_NOT_SAVED;
		}

		return IS_FILE_SAVED;
	}	

	// Splitting the string to get the index to delete the text
	private String deleteFromList(String element){
		
		int index = 0;
		
		try {
			index = Integer.parseInt(element.split(" ", 2)[0]);

		} catch (Exception e) {
			return String.format(MESSAGE_INVALID_COMMAND);
		}
		
		if (index > 0 && textList.size() >= index) {

			return textIsDeleted(index-=1); // TextList index starts from 0

		} else if (textList.isEmpty()){
				return String.format(MESSAGE_FILE_EMPTY, fileName);
		} else {
				return String.format(MESSAGE_INDEX_ERROR);
		}
	}
	
	// Uses the index to delete the text from textList
	private String textIsDeleted(int index){
		
		String deleted = textList.get(index);
		textList.remove(index);
		
		boolean isFileSaved = saveToFile();

		if (isFileSaved) {
			return String.format(MESSAGE_DELETE, fileName, deleted);
		} else { 
			return String.format(MESSAGE_NOT_SAVED);
		}
		
	}
	
	// Display what the textList/file currently contains
	private String displayList(ArrayList<String> list){
		
		StringBuffer result = new StringBuffer();
		if(textList.isEmpty()){
			return String.format(MESSAGE_FILE_EMPTY, fileName);
		} else { 

			for (int i=0; i < list.size(); i++){
				result.append(String.format(MESSAGE_DISPLAY, (i + 1), 
								list.get(i)));
			}
		}

		return result.toString();
		
	}	

	// Clear the whole textList, and saving the change to the file as well
	private String clearList(){

		textList.clear();
		
		boolean isFileSaved = saveToFile();

		if (isFileSaved) {
			return String.format(MESSAGE_CLEAR, fileName);
		} else { 
			return String.format(MESSAGE_NOT_SAVED);
		}		
	}
	
	private String searchList(String element){
		ArrayList<String> searchResults = new ArrayList<String>();
		
		for(String text : textList){
			if(text.contains(element)){
				searchResults.add(text);
			}
		}	
		
		if(searchResults.isEmpty()){
			return String.format(MESSAGE_DOES_NOT_CONTAIN, element);
		}
			
		String result = displayList(searchResults);
		return result.toString();
	}
	
	private String sortList(){
		
		Collections.sort(textList);
		boolean isFileSaved = saveToFile();
		
		if (isFileSaved) {
			return String.format(MESSAGE_SORT);
		} else { 
			return String.format(MESSAGE_NOT_SORTED);
		}
	}
	
}

