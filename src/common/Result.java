package common;

import java.util.ArrayList;

public class Result {
    
    private boolean _isSuccess;
    private String _message;
    private ArrayList<Task> _results;

    public Result() {
        this(false, "Something went wrong!", new ArrayList<Task>());
    }
    
    public Result(boolean isSuccess, String message, ArrayList<Task> results) {
        _isSuccess = isSuccess;
        _message = message;
        _results = results;
    }

    public String getMessage() {
        return _message;
    }
    
    public ArrayList<Task> getResults() {
        return _results;
    }

    public boolean isSuccess() {
        return _isSuccess;
    }
    
    public boolean isSearchCommand() {
        // TODO: improve implementation
        // put somewhere else?
        if (_message.toLowerCase().contains("search")) {
            return true;
        } else {
            return false;
        }
    }

}
