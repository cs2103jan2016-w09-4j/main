package common;

import java.util.ArrayList;
import storage.Storage;

public class AddCommand extends Command{
    private String description;

	public AddCommand(String description) {
	    this.description = description;
	}

	public String getDescription(){
	    return description;
	}

    public ArrayList<Task> execute(Storage storage){
        return storage.addTask(getDescription());
    }
}
