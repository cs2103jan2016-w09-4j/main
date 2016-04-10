//@@author Khanh
package parser;

import common.*;
import common.Command.CommandType;

public class GeneralParser {
    private static final String ADD_COMMAND_CODE = "add";
    private static final String EDIT_COMMAND_CODE = "edit";
    private static final String DELETE_COMMAND_CODE = "delete";
    private static final String SEARCH_COMMAND_CODE = "search";
    private static final String DONE_COMMAND_CODE = "done";
    private static final String SEARCHDONE_COMMAND_CODE = "searchdone";
    private static final String SAVE_COMMAND_CODE = "save";
    private static final String LOAD_COMMAND_CODE = "load";
    private static final String UNDO_COMMAND_CODE = "undo";
    private static final String REDO_COMMAND_CODE = "redo";
    private static final String HOME_COMMAND_CODE = "home";
    private static final String HELP_COMMAND_CODE = "help";
    private static final String EXIT_COMMAND_CODE = "exit";

    private static final String INVALID_COMMAND_NOTIFY = "Invalid command";
    private static final String EMPTY_COMMAND_NOTIFY = "Empty command entered";

    public GeneralParser() {

    }

    private static String getFirstWord(String command) {
        String result = command.trim().split("\\s+", 2)[0];
        return result;
    }

    public Command parseCommand(String commandString) throws EmptyCommandException, InvalidCommandException, WrongCommandFormatException {
        String[] commandParts = commandString.trim().split("\\s+", 2);

        if (commandParts.length==0) {
            throw new EmptyCommandException(EMPTY_COMMAND_NOTIFY);
        }

        String commandCode = commandParts[0].toLowerCase();
        String commandContent = commandString.substring(commandCode.length(), commandString.length());

        if (commandCode.equals(ADD_COMMAND_CODE)) {
            if (commandContent.equals("")) return new Command(CommandType.ADD);
            TaskDetails details = new TaskDetails(commandContent);
            return new Command(CommandType.ADD, details.getDescription(), details.getStartTime(), details.getEndTime(),details.getCategories());

        } else if (commandCode.equals(DELETE_COMMAND_CODE)) {
            return new Command(CommandType.DELETE, Integer.parseInt(commandString.substring(7).trim()));

        } else if (commandCode.equals(EDIT_COMMAND_CODE)) {
            String firstWord = getFirstWord(commandContent);
            String description = commandContent.substring(firstWord.length()).trim();
            System.out.println(commandContent);
            System.out.println(firstWord);
            System.out.println(description);

            TaskDetails details = new TaskDetails(description);
            return new Command(CommandType.EDIT, Integer.parseInt(firstWord),
                    details.getDescription(), details.getStartTime(), details.getEndTime(), details.getCategories());
        } else if (commandCode.equals(DONE_COMMAND_CODE)) {
            return new Command(CommandType.DONE, Integer.parseInt(commandString.substring(4).trim()));

        } else if (commandCode.equals(SEARCHDONE_COMMAND_CODE)) {
            TaskDetails details = new TaskDetails(commandContent);
            return new Command(CommandType.SEARCHDONE, details.getDescription(), details.getStartTime(), details.getEndTime(),
                    details.getCategories());

        } else if (commandCode.equals(SEARCH_COMMAND_CODE)) {
            TaskDetails details = new TaskDetails(commandContent);

            return new Command(CommandType.SEARCH, details.getDescription(), details.getStartTime(), details.getEndTime(),
                    details.getCategories());

        } else if (commandCode.equals(SAVE_COMMAND_CODE)) {
            return new Command(CommandType.SAVE, commandString.substring(4).trim());

        } else if (commandCode.equals(LOAD_COMMAND_CODE)) {
            return new Command(CommandType.LOAD, commandString.substring(4).trim());

        } else if (commandCode.equals(UNDO_COMMAND_CODE)) {
            return new Command(CommandType.UNDO);

        } else if (commandCode.equals(REDO_COMMAND_CODE)) {
            return new Command(CommandType.REDO);

        } else if (commandCode.equals(HOME_COMMAND_CODE)) {
            return new Command(CommandType.HOME);

        } else if (commandCode.equals(HELP_COMMAND_CODE)) {
            return new Command(CommandType.HELP);

        } else if (commandCode.equals(EXIT_COMMAND_CODE)) {
            return new Command(CommandType.EXIT);
        } else {
            throw new InvalidCommandException(INVALID_COMMAND_NOTIFY);
        }
    }
}
