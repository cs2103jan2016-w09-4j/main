package common;

import java.util.ArrayList;

import storage.Storage;


public class EditCommand {
    
	private ArrayList<Task> oldList;	
	
	public EditCommand() {
		
	}
	
	public ArrayList<Task> execute(int id, String newDescription, Storage storage) {
		oldList = storage.editTask(id, newDescription);
		return oldList;
	}
	
}
