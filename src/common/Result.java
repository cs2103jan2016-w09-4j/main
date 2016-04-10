package common;

import java.util.ArrayList;

import common.Command.CommandType;

/**
 * Used by Logic to pass information on the executed command to the
 * UI component. UI uses this to show the appropriate feedback and
 * display tasks to the user.
 */
public class Result {

    private CommandType commandType;
    private boolean isSuccess;
    private String message;
    private ArrayList<Task> results;
    
    public Result() {
        this(CommandType.INVALID, false, "Invalid!", new ArrayList<Task>());
    }
    
    public Result(CommandType commandType, boolean isSuccess, String message, ArrayList<Task> results) {
        this.commandType = commandType;
        this.isSuccess = isSuccess;
        this.message = message;
        this.results = results;
    }

    /******************
     * GETTER METHODS *
     ******************/
    
    public CommandType getCommandType() {
        return commandType;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
    
    public String getMessage() {
        return message;
    }
    
    public ArrayList<Task> getResults() {
        return results;
    }

}
