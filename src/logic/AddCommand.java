package logic;

import java.util.ArrayList;

import logic.TaskList;
import common.Task;
import storage.Storage;

public class AddCommand {

    private TaskList taskList;
    private Storage storage;

    public AddCommand() {

    }

    public ArrayList<Task> execute(Task task) {
        taskList.addTaskIntoList(task.getDescription()); // add for myself
        ArrayList<Task> list = storage.addTask(task.getDescription()); // add
                                                                       // officially
                                                                       // into
                                                                       // storage

        return list;
    }

}