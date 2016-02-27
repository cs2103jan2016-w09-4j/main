package logic;

import java.util.ArrayList;

import Logic.TaskList;

public class AddCommand
{
	private Task task;
	private TaskList taskList;

	public AddCommand()
	{
		
		
		
	}
	
	public ArrayList<Task> execute(Task task){
		
		Task addTask = taskList.addTask(task.getDescription()); // this one needs to create a Task inside the Task class first, then the task class returns the Task here
		ArrayList<Task> list = storage.addTask(task.getDescription());
		
		return list;
		
		
	}
	
	
}