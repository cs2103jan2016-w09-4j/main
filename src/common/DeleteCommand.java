package common;

import java.util.ArrayList;

import storage.Storage;

public class DeleteCommand {

    private Task task;
	private Storage storage = new Storage();
	
	// constructor
	public DeleteCommand() {
		
	}
	
	public ArrayList<Task> execute(int id) {
		ArrayList<Task> list = storage.deleteTask(id);
		return list;
	}
	
}
