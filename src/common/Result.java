package common;

import java.util.ArrayList;

import common.Command.CommandType;

public class Result {

    private static int NO_NEW_TASK_ID = -1;

    private CommandType commandType;
    private boolean isSuccess;
    private String message;
    private ArrayList<Task> results;
    private int newTaskId;
    
    public Result() {
        this(CommandType.INVALID, false, "Invalid command!", new ArrayList<Task>(), NO_NEW_TASK_ID);
    }
    
    public Result(CommandType commandType, boolean isSuccess, String message, ArrayList<Task> results) {
        this(commandType, isSuccess, message, results, NO_NEW_TASK_ID);
    }
    
    public Result(CommandType commandType, boolean isSuccess, String message, ArrayList<Task> results, int newTaskId) {
        this.commandType = commandType;
        this.isSuccess = isSuccess;
        this.message = message;
        this.results = results;
        this.newTaskId = newTaskId;
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
    
    public int getHighlight() {
        return newTaskId;
    }

}
