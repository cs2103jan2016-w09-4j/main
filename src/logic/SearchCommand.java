package logic;

import java.util.ArrayList;

import common.Task;
import storage.Storage;

public class SearchCommand {
    
    private Storage storage;
    private Task task;
    
    public SearchCommand() {
        
    }
    
    public ArrayList<Task> execute(String keyword) {
        ArrayList<Task> searchResult = storage.searchTask(keyword);
        return searchResult;
    }
    
}