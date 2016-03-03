package common;

import java.util.ArrayList;
import storage.Storage;

public class EditCommand extends Command{
    private int id;
    private String description;

	public EditCommand(int id, String description) {
	    this.id = id;
	    this.description = description;
	}

	public int getId(){
	    return id;
	}

	public String getDescription(){
	    return description;
	}

    public ArrayList<Task> execute(Storage storage){
        return storage.editTask(getId(), getDescription());
    }
}
