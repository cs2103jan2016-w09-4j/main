import storage.Storage;


public class DeleteCommand
{
	@SuppressWarnings("unused")
	private Task task;
	private Storage storage;
	
	
	// constructor
	public DeleteCommand()  
	{
		
	}
	
	public ArrayList<Task> execute(int id)
	{
		ArrayList<Task> list = storage.deleteTask(id);
		
		return list;
	}
	
	
}
