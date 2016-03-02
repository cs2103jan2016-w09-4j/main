package common;

import java.util.ArrayList;

import common.Task;
import storage.Storage;

public class AddCommand {
    
	private Storage storage = new Storage();

	public AddCommand() {
	    // TODO
	}
	
	public ArrayList<Task> execute(String description) {
		ArrayList<Task> list = storage.addTask(description);  // add officially into storage
		return list;
	}
	
}