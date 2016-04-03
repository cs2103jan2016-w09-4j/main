//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import common.*;
import common.Command.CommandType;

public class GeneralParser {

    public GeneralParser() {

    }

    private static String getFirstWord(String command) {
        String result = command.trim().split("\\s+", 2)[0];
        return result;
    }

    public Command parseCommand(String commandString) {
        String command = commandString.trim().toLowerCase();

        if (command.startsWith("add")) {
            DescriptionParser details = new DescriptionParser(commandString.substring(4).trim(),"add");
            return new Command(CommandType.ADD, details.getDescription(), details.getStartTime(), details.getEndTime());
        } else if (command.startsWith("delete")) {
            return new Command(CommandType.DELETE, Integer.parseInt(commandString.substring(7).trim()));
        } else if (command.startsWith("edit")) {
            String content = commandString.substring(5);
            String firstWord = getFirstWord(content);
            String description = content.substring(firstWord.length()).trim();

            DescriptionParser details = new DescriptionParser(description,"edit");
            return new Command(CommandType.EDIT, Integer.parseInt(firstWord),
                    details.getDescription(), details.getStartTime(), details.getEndTime());
        } else if (command.startsWith("done")) {
            return new Command(CommandType.DONE, Integer.parseInt(commandString.substring(4).trim()));
        } else if (command.startsWith("searchdone")) {
        	String content = getUserInput(command,"searchdone");  
            DescriptionParser details = new DescriptionParser(content,"searchdone");
            
            if (details.getSearchTime() != null) {
            	return new Command(CommandType.SEARCHDONE, details.getSearchTime());
            } else if (details.getCategories() != null) {
            	return new Command(CommandType.SEARCHDONE, details.getCategories());
            }      
            return new Command(CommandType.SEARCHDONE, details.getDescription());

        } else if (command.startsWith("search")) {
            String content = getUserInput(command,"search");  
            DescriptionParser details = new DescriptionParser(content,"search");
            
            if (details.getSearchTime() != null) {
            	return new Command(CommandType.SEARCH, details.getSearchTime());
            } else if (details.getCategories() != null) {
            	return new Command(CommandType.SEARCH, details.getCategories());
            }
            
            return new Command(CommandType.SEARCH, details.getDescription());
   
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
    }

    private static String getUserInput(String line, String toReplace) {
        String userInput = line.replace(toReplace, "").trim();

        return userInput;
    }
}
