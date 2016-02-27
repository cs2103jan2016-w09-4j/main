package logic;

import java.util.ArrayList;

import logic.TaskList;
import logic.Task;
import storage.Storage;


public class DeleteCommand
{
	@SuppressWarnings("unused")
	private Task task;
	private TaskList taskList;
	private Storage storage;
	
	
	// constructor
	public DeleteCommand(Task task)  
	{
		this.task = task;
		
	}
	
	public ArrayList<Task> execute(int id)
	{
		ArrayList<Task> list = storage.deleteTask(id);
		taskList.deletedTask(list);
		
		return list;
	}
	
	
}