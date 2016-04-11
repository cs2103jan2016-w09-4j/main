package gridtask.common;

import gridtask.common.Command.CommandType;
import java.util.ArrayList;

/**
 * Contains information about the command that was executed.
 */
public class Result {

    // Type of command that was executed
    private CommandType commandType;
    // Whether the command was successful
    private boolean isSuccess;
    // Feedback message
    private String message;
    // List of tasks updated by the command
    private ArrayList<Task> results;
    
    public Result() {
        this(CommandType.INVALID, false, "Invalid!", new ArrayList<Task>());
    }

    public Result(CommandType commandType, boolean isSuccess,
                  String message, ArrayList<Task> results) {
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
    
    /**
     * Returns the list of tasks updated by the command.
     * If it is a search command, the list will contain search results.
     */
    public ArrayList<Task> getResults() {
        return results;
    }

}
