package common;

import java.util.ArrayList;

public class Result {
    
    private boolean isSuccess;
    private String message;
    private ArrayList<Task> results;

    public Result() {
        this(false, "Something went wrong!", new ArrayList<Task>());
    }
    
    public Result(boolean isSuccess, String message, ArrayList<Task> results) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.results = results;
    }

    /******************
     * GETTER METHODS *
     ******************/
    
    public String getMessage() {
        return message;
    }
    
    public ArrayList<Task> getResults() {
        return results;
    }

    /******************
     * HELPER METHODS *
     ******************/

    public boolean isSuccess() {
        return isSuccess;
    }
    
    public boolean isSearchCommand() {
        // TODO: improve implementation
        if (message.toLowerCase().contains("search")) {
            return true;
        } else {
            return false;
        }
    }

}
