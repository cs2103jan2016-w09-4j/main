package application;

import java.util.ArrayList;

public class Feedback {
    
    private boolean _isSuccess;
    private String _message;
    private ArrayList<Task> _results;
    
    public Feedback(boolean isSuccess, String message, ArrayList<Task> results) {
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
        if (_message.toLowerCase().contains("search")) {
            return true;
        } else {
            return false;
        }
    }

}
