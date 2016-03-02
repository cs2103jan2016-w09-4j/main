package common;

import java.util.ArrayList;

import common.Task;
import storage.Storage;

public class AddCommand {
    

	public AddCommand() {
	    // TODO
	}
	
	public ArrayList<Task> execute(String description, Storage storage) {
		ArrayList<Task> list = storage.addTask(description);  // add officially into storage
		return list;
	}
	
}
