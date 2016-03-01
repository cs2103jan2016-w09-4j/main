package logic;

import java.util.ArrayList;

import storage.Storage;
import common.Task;

public class SearchCommand
{
	
	private Storage storage;
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
