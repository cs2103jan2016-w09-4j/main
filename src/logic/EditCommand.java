package logic;

import java.util.ArrayList;

import logic.TaskList;

public class EditCommand
{
	
	private Task task;
	private ArrayList<Task> oldList;	
	
	public EditCommand(TaskList taskList)
	{
		this.oldList = taskList.getTaskList();
		
		
	}
	
	public ArrayList<Task> execute()
	{
		
		
		
		
	}
	
}