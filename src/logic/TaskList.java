package logic;


import java.util.ArrayList;

import common.Task;
import storage.Storage;

//need to import other classes *****
//command, result, parser, storage, etc.

// This class get the list of Task from storage and hold it so that Logic class can use it from here.
// It also contains some parts of the command executions

public class TaskList
{
	
	@SuppressWarnings("unused")
	private int id;
	private Storage storage;
	private ArrayList<Task> list;
	
	
	public TaskList()
	{
		this.id = 0;
		this.list = storage.getMainList();
	}
	
	// add the task into the list, then return the task 
	public void addTaskIntoList(String description)
	{
		
		Task task = new Task(description);
		this.list.add(task);
		
	}
	
	public void deletedTask(ArrayList<Task> list)
	{
		if(!checkEmpty(list)){
		
			setTaskList(list);  // update the list
		}
		
		
	}
	
	public boolean checkEmpty(ArrayList<Task> list)
	{
		ArrayList<Task> thisList = getTaskList();
		if(thisList != null)
		{
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Task> getTaskList()
	{
		return this.list;
	}
	
	public void setTaskList(ArrayList<Task> list)
	{
		this.list = list;
	}
	

}
