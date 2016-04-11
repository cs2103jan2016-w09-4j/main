//@@author A0098084U
package gridtask.parser;

import gridtask.common.Command;
import gridtask.common.Command.CommandType;

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
    private static final String INVALID_TASK_ID_NOTIFY = "Invalid task ID";
    private static final String EMPTY_COMMAND_NOTIFY = "Empty command entered";

    public GeneralParser() {

    }

    public Command parseCommand(String commandString) throws EmptyCommandException, InvalidCommandException, WrongCommandFormatException {
        String[] commandParts = getCommandCodeAndContent(commandString);

        String commandCode = commandParts[0].toLowerCase();
        String commandContent = "";
        if (commandParts.length>=2) commandContent = commandParts[1];

        if (commandCode.equals(EDIT_COMMAND_CODE)) {
            return createEditCommand(commandContent);

        } else if (commandCode.equals(ADD_COMMAND_CODE)) {
            return createCommandWithDescription(CommandType.ADD, commandContent);

        } else if (commandCode.equals(SEARCHDONE_COMMAND_CODE)) {
            return createCommandWithDescription(CommandType.SEARCHDONE, commandContent);

        } else if (commandCode.equals(SEARCH_COMMAND_CODE)) {
            return createCommandWithDescription(CommandType.SEARCH, commandContent);

        } else if (commandCode.equals(DELETE_COMMAND_CODE)) {
            return createCommandWithId(CommandType.DELETE, commandContent);

        } else if (commandCode.equals(DONE_COMMAND_CODE)) {
            return createCommandWithId(CommandType.DONE, commandContent);
        }

        else if (commandCode.equals(SAVE_COMMAND_CODE)) {
            return new Command(CommandType.SAVE, commandContent);

        } else if (commandCode.equals(LOAD_COMMAND_CODE)) {
            return new Command(CommandType.LOAD, commandContent);

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

    /**
     * Returns a command of type EDIT
     */
    public Command createEditCommand(String commandContent) throws WrongCommandFormatException {
        String firstWord = getFirstWord(commandContent);
        try {
            int taskId = Integer.parseInt(firstWord);

            String description = commandContent.substring(firstWord.length()).trim();

            TaskDetails details = new TaskDetails(description);
            return new Command(CommandType.EDIT, taskId, details.getDescription(), 
                    details.getStartTime(), details.getEndTime(), details.getCategories());
        } catch (NumberFormatException e) {
            throw new WrongCommandFormatException(INVALID_TASK_ID_NOTIFY);
        }
    }

    /**
     * Returns a command that contains a description
     * The input can contains: description, time (start and end) and categories
     * Used to create the following commands: add, search, searchdone
     */
    public Command createCommandWithDescription(CommandType commandType, String commandContent) throws WrongCommandFormatException {
        TaskDetails details = new TaskDetails(commandContent);
        return new Command(commandType, details.getDescription(), details.getStartTime(), details.getEndTime(),details.getCategories());
    }

    /**
     * Returns a Command that only has task ID.
     * Used to create the following commands: delete, done
     */
    public Command createCommandWithId(CommandType commandType, String commandContent) throws WrongCommandFormatException {
        try {
            return new Command(commandType, Integer.parseInt(commandContent));
        } catch (NumberFormatException e) {
            throw new WrongCommandFormatException(INVALID_TASK_ID_NOTIFY);
        }
    }

    /**
     * @param commandString
     * @return commandParts[0] is code, commandParts[2] is content of command
     * @throws EmptyCommandException
     */

    public String[] getCommandCodeAndContent(String commandString) throws EmptyCommandException {
        String[] commandParts = commandString.trim().split("\\s+", 2);

        if (commandParts.length==0) {
            throw new EmptyCommandException(EMPTY_COMMAND_NOTIFY);
        }
        return commandParts;
    }

    private static String getFirstWord(String command) {
        String result = command.trim().split("\\s+", 2)[0];
        return result;
    }
    
}