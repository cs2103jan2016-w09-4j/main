package logic;


import java.util.ArrayList;

//need to import other classes *****
//command, result, parser, storage, etc.

// This class get the list of Task from storage and hold it so that Logic class can use it from here.
// It also contains some parts of the command executions

public class TaskList
{
	
	private int id;
	private Storage storage;
	private ArrayList<Task> list;
	
	
	public TaskList(Storage storage)
	{
		this.id = 0;
		this.storage = storage;
		this.list = storage.getTaskList();
	}
	
	// add the task into the list, then return the task 
	public Task addTask(Task description)
	{
		
		Task task = new Task(description);
		this.list.add(task);
		
		return task;
	}
	
	public void deleteTask(ArrayList<Task> list)
	{
		if(!checkEmpty(list)){
		
			for(Task task : list)
			{
			
				if(task.getTaskId() == id)
				{
					list.remove(id);
				
				}
			}
		}
		
		setTaskList(list);  // update the list
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
	
	public void exit()
	{
		this.storage.saveTasks(this.list);
		// is that all...?
		
	}
}