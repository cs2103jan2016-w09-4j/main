package common;

import java.util.ArrayList;

import storage.Storage;

public class DeleteCommand {

    private Task task;
	
	// constructor
	public DeleteCommand() {
		
	}
	
	public ArrayList<Task> execute(int id, Storage storage) {
		ArrayList<Task> list = storage.deleteTask(id);
		return list;
	}
	
}
