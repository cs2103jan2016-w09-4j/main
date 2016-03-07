package common;

import java.util.ArrayList;
import storage.Storage;

public class SearchCommand extends Command{
	private String keyword;

	public SearchCommand(String keyword) {
	    this.keyword = keyword;

	}

	public String getKeyword(){
	    return keyword;
	}

    public ArrayList<Task> execute(Storage storage){
        return storage.searchTask(getKeyword());
    }
}
