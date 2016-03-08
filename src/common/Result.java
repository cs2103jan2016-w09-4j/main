package common;

import java.util.ArrayList;

import common.Command.CommandType;

public class Result {
    
    private CommandType commandType;
    private boolean isSuccess;
    private ArrayList<Task> results;

    public Result() {
        this(CommandType.INVALID, false, new ArrayList<Task>());
    }
    
    public Result(CommandType commandType, boolean isSuccess, ArrayList<Task> results) {
        this.commandType = commandType;
        this.isSuccess = isSuccess;
        this.results = results;
    }

    /******************
     * GETTER METHODS *
     ******************/
    
    public CommandType getCommandType() {
        return commandType;
    }
    
    public ArrayList<Task> getResults() {
        return results;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

}
