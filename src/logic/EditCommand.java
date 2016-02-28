package logic;

import java.util.ArrayList;

import logic.TaskList;
import common.Task;
import storage.Storage;

public class EditCommand {
    
    private Storage storage;
    private ArrayList<Task> oldList;    
    
    public EditCommand(TaskList taskList) {
        this.oldList = taskList.getTaskList();
    }
    
    public ArrayList<Task> execute(int id, String newDescription) {
        oldList = storage.editTask(id, newDescription);
        return oldList;
    }
    
}