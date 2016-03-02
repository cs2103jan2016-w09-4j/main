package common;

import java.util.ArrayList;

import storage.Storage;


public class EditCommand {
    
	private Storage storage = new Storage();
	private ArrayList<Task> oldList;	
	
	public EditCommand() {
		
	}
	
	public ArrayList<Task> execute(int id, String newDescription) {
		oldList = storage.editTask(id, newDescription);
		return oldList;
	}
	
}
