package common;

import java.util.ArrayList;

import common.Command.CommandType;

public class Result {
    
    private CommandType commandType;
    private boolean isSuccess;
    private String message;
    private ArrayList<Task> results;

    public Result() {
        this(CommandType.INVALID, false, "Invalid command!", new ArrayList<Task>());
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
