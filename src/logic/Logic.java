package logic;

import common.*;
import common.Command.CommandType;
import parser.Parser;
import storage.Storage;
import logic.Execution;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class Logic {

    // Objects to call into other classes
    private Parser parser;
    private Storage storage;
    private Execution execution;
    private static Logic logic = new Logic();
    
    private ArrayList<Task> list;
    private static final int MAX_PREDICTIONS = 5;
    
    public Logic() {
        this.parser = new Parser();
        this.storage = new Storage();
        this.execution = new Execution();
        this.list = storage.getMainList();
    }

    private Result execute(Command command){
        
        CommandType commandType = command.getType();
        String description = command.getDescription();
        int taskID = command.getId();       
        
        switch(commandType) {
        
            case ADD :
                return execution.addTask(description, command.getStartDate(), command.getEndDate());
            
            case DELETE :
                return execution.deleteTask(taskID);
            
            case EDIT :
                return execution.editTask(taskID, description, command.getStartDate(), command.getEndDate());
                
            case SEARCH :
                return execution.searchTask(description);
            
            case HOME :
                list = storage.getMainList();
                execution.setMainList(list);
                
                return new Result(commandType, true, "Return home", list);
                
            case SAVE :
                execution.savingTasks(description);
                list = storage.getMainList();
                return new Result(commandType, true, "Saved at " + description, list);
                
            case LOAD :
                return execution.loadingTasks(description);

            case UNDO :
                list = execution.undoCommand();
                return new Result(commandType, true, "Last command undone", list);
                
            case REDO : 
                list = execution.redoCommand();
                return new Result(commandType, true, "Last command redone", list);
                
            case DONE :
                return execution.completeCommand(taskID);
                
            case SEARCHDONE :
                list = execution.getDoneList();
                return new Result(commandType, true, "Showing completed tasks", list);

            case HELP :
                return new Result(commandType, true, "Help", null);
                
            case EXIT :
                System.exit(0);
            
            case INVALID :
                return new Result();
                
            default :
                return new Result();
                
        }
    }

    public Result processCommand(String input) {
        System.out.println(input);
        Command command = parser.parseCommand(input);
        return execute(command);
    }
    
    public ArrayList<String> getPredictions(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        ArrayList<String> predictions = new ArrayList<String>();
        String[] params = input.split("\\s+", 2);
        String firstWord = params[0];
        if (firstWord.equalsIgnoreCase("add")) {
            TreeSet<String> dictionary = execution.getDictionary();
            if (params.length == 1) {
                if (dictionary.size() > 1) {
                    predictions.add(firstWord + " " + dictionary.first());
                    predictions.add(firstWord + " " + dictionary.last());
                } else if (dictionary.size() > 0) {
                    predictions.add(firstWord + " " + dictionary.first());
                }
            } else {
                // retrieve entry matching user input
                String argument = params[1];
                SortedSet<String> matches = dictionary.tailSet(argument);
                for (String entry : matches) {
                    predictions.add(firstWord + " " + entry);
                    if (predictions.size() >= MAX_PREDICTIONS) {
                        break;
                    }
                }
            }
        } else if (firstWord.equalsIgnoreCase("search")) {
            TreeSet<String> dictionary = execution.getDictionary();
            if (dictionary.size() > 1) {
                predictions.add(firstWord + " " + dictionary.first());
                predictions.add(firstWord + " " + dictionary.last());
            } if (dictionary.size() > 0) {
                predictions.add(firstWord + " " + dictionary.first());
            }
        } else if (firstWord.equalsIgnoreCase("edit")) {
            if (params.length == 2) {
                // retrieve task description
                try {
                    int id = Integer.parseInt(params[1]);
                    predictions.add(firstWord + " " + id + " " + execution.getMainList().get(id-1).getDescription());
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    // no predictions
                }
            }
        }
        return predictions;
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
