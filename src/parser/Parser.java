//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import common.*;
import common.Command.CommandType;

public class Parser {

    public Parser() {

    }

    private static String getFirstWord(String s) {
        String result = s.trim().split("\\s+")[0];
        return result;
    }

    public Command parseCommand(String commandString) {
        String command = commandString.trim().toLowerCase();

        try {
            if (command.startsWith("add")) {
                DescriptionParser details = new DescriptionParser(commandString.substring(4).trim());
                return new Command(CommandType.ADD, details.getDescription(), details.getStartTime(), details.getEndTime());
            } else if (command.startsWith("delete")) {
                return new Command(CommandType.DELETE, Integer.parseInt(commandString.substring(7).trim()));
            } else if (command.startsWith("edit")) {
                String content = commandString.substring(5);
                String firstWord = getFirstWord(content);
                String description = content.substring(firstWord.length()).trim();

                DescriptionParser details = new DescriptionParser(description);
                return new Command(CommandType.EDIT, Integer.parseInt(firstWord),
                        details.getDescription(), details.getStartTime(), details.getEndTime());
            } else if (command.startsWith("done")) {
                return new Command(CommandType.DONE, Integer.parseInt(commandString.substring(4).trim()));
            } else if (command.startsWith("searchdone")) {
                return new Command (CommandType.SEARCHDONE);
            } else if (command.startsWith("search")) {
            	String userInput = getUserInput(command,"search");
            	String firstCharacter = String.valueOf(userInput.charAt(0));
            	ArrayList<String> categories = new ArrayList<String>();
            	// search by category
            	if (firstCharacter.equals("#")) {
            		String[] categoryLine = userInput.split(" ");
            		String categoryName;
            		if (categoryLine.length == 1) {					// only one category
            			categoryName = getUserInput(userInput,"#");
            			categories.add(categoryName);
            		} else {										// multiple categories
            			for (int i=0; i<categoryLine.length; i++) {
            				categoryName = getUserInput(categoryLine[i],"#");
            				categories.add(categoryName);
            			}
            		}		
            		return new Command(CommandType.SEARCH,categories);
            	} else {
            		try{
            			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            			LocalDateTime date = LocalDateTime.parse(userInput, formatter);
            			return new Command(CommandType.SEARCH,date);	
            		} catch (DateTimeParseException dtpe) {			//description
            			return new Command(CommandType.SEARCH,userInput);
            		}
            	}
            } else if (command.startsWith("save")) {
                return new Command(CommandType.SAVE, commandString.substring(4).trim());
            } else if (command.startsWith("load")) {
                return new Command(CommandType.LOAD, commandString.substring(4).trim());
            } else if (command.startsWith("undo")) {
                return new Command(CommandType.UNDO);
            } else if (command.startsWith("redo")) {
                return new Command(CommandType.REDO);
            } else if (command.startsWith("home")) {
                return new Command(CommandType.HOME);
            } else if (command.startsWith("help")) {
                return new Command(CommandType.HELP);
            } else if (command.startsWith("exit")) {
                return new Command(CommandType.EXIT);
            } else {
                return new Command(CommandType.INVALID);
            }
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return new Command(CommandType.INVALID);
        }
    }
    
    private static String getUserInput(String line, String toReplace) {
		String userInput = line.replace(toReplace, "").trim();

		return userInput;
	}

}
