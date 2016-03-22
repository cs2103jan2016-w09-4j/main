package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import logic.Execution;

import java.util.ArrayList;

public class Logic {

    // Objects to call into other classes
    private Parser parser;
    private Storage storage;
    private Execution execution;
    private static Logic logic = new Logic();
    
    public Logic() {
        this.parser = new Parser();
        this.storage = new Storage();
        this.execution = new Execution();
    }

    private Result execute(Command command){
        ArrayList<Task> list = new ArrayList<Task>();
        
        CommandType commandType = command.getType();
        String description = command.getDescription();
        int taskID = command.getId();       
        
        switch(commandType) {
        
            case ADD :
                list = execution.addTask(description);
                return new Result(commandType, true, "Added task", list);
            
            case DELETE :
                list = execution.deleteTask(taskID);
                return new Result(commandType, true, "Deleted task", list);
            
            case EDIT :
                list = execution.editTask(taskID, description);
                return new Result(commandType, true, "Edited task", list);
                
            case SEARCH :
                list = execution.searchTask(description);
                System.out.println(getPredictions());
                return new Result(commandType, true, "Searched tasks", list);
            
            case HOME :
                list = execution.getMainList();
                return new Result(commandType, true, "Return home", list);
                
            case SAVE :
                execution.savingTasks(description);
                list = execution.getMainList();
                return new Result(commandType, true, "Saved at " + description, list);
                
            case LOAD :
                list = execution.loadingTasks(description);
                return new Result(commandType, true, "Loaded from " + description, list);

            case UNDO :
                list = execution.undoCommand();
                return new Result(commandType, true, "Last command undone", list);
                
            case REDO : 
                list = execution.redoCommand();
                return new Result(commandType, true, "Last command redone", list);
                
            case COMPLETE :
                list = execution.completeCommand(taskID);
                return new Result(commandType, true, "Marked as complete", list);
                
            case DONE :
                list = execution.getDoneList();
                return new Result(commandType, true, "Showing completed tasks", list);
                
            case EXIT :
                System.exit(0);
            
            case INVALID :
                list = null;
                return new Result();
                
            default :
                list = null;
                return new Result();
                
        }
    }

    public Result processCommand(String input) {
        System.out.println(input);
        Command command = parser.parseCommand(input);
        return execute(command);
    }
    
    public ArrayList<String> getPredictions(){
        return execution.getDictionary();
    }
    
    public ArrayList<String> getPredictions(String input) {
        if (input.isEmpty()) {
            return null;
        } else {
            return getPredictions();
        }
    }
    
    public ArrayList<Task> getMainList() {
        return execution.getMainList();
    }
    
    public static Logic getInstance() {
        if (logic == null){
            return logic = new Logic();
        }
        return logic;
    }

    public ArrayList<Category> getCategories() {
        return execution.getCategories();
    }
    
}
