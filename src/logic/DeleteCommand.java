package logic;

import java.util.ArrayList;

import logic.TaskList;


public class DeleteCommand
{

	private Storage storage;
	private Task task;
	private TaskList taskList;
	
	
	// constructor
	public DeleteCommand(Task task)  
	{
		this.task = task;
		
	}
	
	public ArrayList<Task> execute(int id)
	{
		ArrayList<Task> list = storage.deleteTask(taskList);
		taskList.deleteTask(list);
		
		return list;
	}
	
	
}