package common;

import java.util.ArrayList;

import storage.Storage;

public class SearchCommand
{
	
	private Storage storage = new Storage();
//	private Task task;
	
	public SearchCommand() // constructor
	{
		
		
	}
	
	
	public ArrayList<Task> execute(String keyword)
	{
		ArrayList<Task> searchResult = storage.searchTask(keyword);
		
		return searchResult;
		
	}
	
}
