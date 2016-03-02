package parser;

import common.*;

public class Parser {

    public Parser(){

    }

    private static String getFirstWord(String s){
        String result = s.trim().split("\\s+")[0];
        return result;
    }

    public Command parseCommand(String commandStr){
        if (commandStr.startsWith("add")){
            return new AddCommand(commandStr.substring(4).trim());
        } else if (commandStr.startsWith("delete")){
            return new DeleteCommand(Integer.parseInt(commandStr.substring(7)));
        } else if (commandStr.startsWith("edit")){
            String content = commandStr.substring(5);
            String firstWord = getFirstWord(content);
            String description = content.substring(firstWord.length()).trim();
            return new EditCommand(Integer.parseInt(firstWord), description);
        } else if (commandStr.startsWith("search")){
            return new SearchCommand(commandStr.substring(6).trim());
        } else {
            return new Command();
        }
    }

}
