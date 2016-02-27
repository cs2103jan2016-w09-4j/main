package logic;

import java.util.ArrayList;

import logic.TaskList;

// Executes the exit command, first saving the taskList first
public class ExitCommand
{
	
	private Task task;
	private TaskList taskList;
	
	public void execute()
	{
		taskList.exit();
	}
	
	
}