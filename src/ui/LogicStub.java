package ui;


/* |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 * ||                                                                     ||
 * ||  Dummy class for UI testing until methods are implemented in Logic  ||
 * ||                                                                     ||
 * |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
 */

import java.util.ArrayList;
import java.util.Arrays;

import common.Command.CommandType;
import common.Result;
import common.Task;

public class LogicStub {

    private static String ADD_STRING = "Find the answer to life, the "
            + "universe, and everything";
    private static String SEARCH_STRING = "Lorem ipsum dolor sit amet "
            + "dolores umbridge eco llama";

    public static ArrayList<String> autoComplete(String input) {
        input = input.toLowerCase();
        if (input.startsWith("a") || input.startsWith("ad") || input.startsWith("add")) {
            return new ArrayList<String>(Arrays.asList("add ..."));
        } else if (input.startsWith("d") || input.startsWith("delete")) {
            return new ArrayList<String>(Arrays.asList("delete ..."));
        } else if (input.startsWith("e")) {
            return new ArrayList<String>(Arrays.asList("edit ...", "exit"));
        } else {
            return new ArrayList<String>();
        }
    }
    
    public static Result executeAddCommand(String input) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task(ADD_STRING));
        }
        return new Result(CommandType.ADD, true, "Added task", tasks);
    }
    
    public static Result executeSearchCommand(String input) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task(SEARCH_STRING));
        }
        return new Result(CommandType.SEARCH, true, "Search for task", tasks);
    }

    public static ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task("Fite me irl"));
        }
        return tasks;
    }
}