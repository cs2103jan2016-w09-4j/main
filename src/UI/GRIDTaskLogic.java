package ui;

import java.util.ArrayList;

import common.Category;
import common.Result;
import common.Task;

public class GRIDTaskLogic {

    public static Result executeCommand(String input) {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task());
        }
        return new Result(true, "Added task!", tasks);
    }

    public static ArrayList<Category> getCategories() {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (int i = 0; i < 3; i++) {
            cats.add(new Category());
        }
        return cats;
    }
    
    public static ArrayList<Task> getTodayTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 1; i++) {
            tasks.add(new Task());
        }
        return tasks;
    }

    public static ArrayList<Task> getOtherTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for (int i = 0; i < 5; i++) {
            tasks.add(new Task());
        }
        return tasks;
    }
}