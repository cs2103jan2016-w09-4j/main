package ui;

/* |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 * ||                                                                     ||
 * ||  Dummy class for UI testing until methods are implemented in Logic  ||
 * ||                                                                     ||
 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 */
import java.util.ArrayList;

import common.Category;
import common.Result;
import common.Task;
import logic.Logic;

public class GRIDTaskLogic {

    private static String ADD_STRING = "Find the answer to life, the "
            + "universe, and everything";
    private static String SEARCH_STRING = "Lorem ipsum dolor sit amet "
            + "dolores umbridge eco llama";
    
    private static Logic logic;

    public static Result processCommand(String input) {
    	if (input.length()%2 == 1) {    
    		return executeAddCommand(input);		    
        } else {		 
             return executeSearchCommand(input);	
        }	
    }	
    
    public static Result executeAddCommand(String input) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task(ADD_STRING));
        }
        return new Result(true, "Added task!", tasks);
    }
    
    public static Result executeSearchCommand(String input) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task(SEARCH_STRING));
        }
        return new Result(true, "Searched task!", tasks);
    }

    public static ArrayList<Category> getCategories() {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (int i = 0; i < 3; i++) {
            cats.add(new Category());
        }
        return cats;
    }
    
    public static ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task("Vote for next Android version to be Nutella"));
        }
        return tasks;
    }
}
