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

    public Command parseCommand(String commandStr) {
        commandStr = commandStr.trim().toLowerCase();
        
        try {
            if (commandStr.startsWith("add")){
                return new Command(CommandType.ADD, commandStr.substring(4).trim());
            } else if (commandStr.startsWith("delete")) {
                return new Command(CommandType.DELETE, Integer.parseInt(commandStr.substring(7)));
            } else if (commandStr.startsWith("edit")) {
                String content = commandStr.substring(5);
                String firstWord = getFirstWord(content);
                String description = content.substring(firstWord.length()).trim();
                return new Command(CommandType.EDIT, Integer.parseInt(firstWord), description);
            } else if (commandStr.startsWith("complete")) {
                return new Command(CommandType.COMPLETE, Integer.parseInt(commandStr.substring(5)));
            } else if (commandStr.startsWith("done")) {
                return new Command (CommandType.DONE);
            } else if (commandStr.startsWith("search")) {
                return new Command(CommandType.SEARCH, commandStr.substring(6).trim());
            } else if (commandStr.startsWith("save")) {
                return new Command(CommandType.SAVE, commandStr.substring(4).trim());
            } else if (commandStr.startsWith("load")) {
                return new Command(CommandType.LOAD, commandStr.substring(4).trim());
            } else if (commandStr.startsWith("undo")) {
                return new Command(CommandType.UNDO);
            } else if (commandStr.startsWith("redo")) {
                return new Command(CommandType.REDO);
            } else if (commandStr.startsWith("home")) {
                return new Command(CommandType.HOME);
            } else if (commandStr.startsWith("exit")) {
                return new Command(CommandType.EXIT);
            } else {
                return new Command(CommandType.INVALID);
            }
        } catch (NumberFormatException nfe) {
            return new Command(CommandType.INVALID);
        }
    }

}
