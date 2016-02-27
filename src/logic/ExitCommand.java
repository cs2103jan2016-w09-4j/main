package logic;


import logic.TaskList;


// Executes the exit command, first saving the taskList first
public class ExitCommand
{
	private TaskList taskList;
	
	public void execute()
	{
		taskList.exit();
	}
	
	
}