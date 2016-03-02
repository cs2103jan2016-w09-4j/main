package common;

import java.util.ArrayList;

import storage.Storage;

public class SearchCommand
{
	
	public SearchCommand() // constructor
	{
		
		
	}
	
	
	public ArrayList<Task> execute(String keyword, Storage storage)
	{
		ArrayList<Task> searchResult = storage.searchTask(keyword);
		
		return searchResult;
		
	}
	
}
