package parser;

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
        commandString = commandString.trim().toLowerCase();

        try {
            if (commandString.startsWith("add")) {
                DescriptionParser details = new DescriptionParser(commandString.substring(4).trim());
                return new Command(CommandType.ADD, details.getDescription(), details.getStartTime(), details.getEndTime());
            } else if (commandString.startsWith("delete")) {
                return new Command(CommandType.DELETE, Integer.parseInt(commandString.substring(7).trim()));
            } else if (commandString.startsWith("edit")) {
                String content = commandString.substring(5);
                String firstWord = getFirstWord(content);
                String description = content.substring(firstWord.length()).trim();

                DescriptionParser details = new DescriptionParser(description);
                return new Command(CommandType.EDIT, Integer.parseInt(firstWord),
                        details.getDescription(), details.getStartTime(), details.getEndTime());
            } else if (commandString.startsWith("complete")) {
                return new Command(CommandType.COMPLETE, Integer.parseInt(commandString.substring(9).trim());
            } else if (commandString.startsWith("done")) {
                return new Command (CommandType.DONE);
            } else if (commandString.startsWith("search")) {
                return new Command(CommandType.SEARCH, commandString.substring(6).trim());
            } else if (commandString.startsWith("save")) {
                return new Command(CommandType.SAVE, commandString.substring(4).trim());
            } else if (commandString.startsWith("load")) {
                return new Command(CommandType.LOAD, commandString.substring(4).trim());
            } else if (commandString.startsWith("undo")) {
                return new Command(CommandType.UNDO);
            } else if (commandString.startsWith("redo")) {
                return new Command(CommandType.REDO);
            } else if (commandString.startsWith("home")) {
                return new Command(CommandType.HOME);
            } else if (commandString.startsWith("exit")) {
                return new Command(CommandType.EXIT);
            } else {
                return new Command(CommandType.INVALID);
            }
        } catch (NumberFormatException nfe) {
            return new Command(CommandType.INVALID);
        }
    }

}
