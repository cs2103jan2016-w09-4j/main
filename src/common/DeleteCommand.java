package common;

import java.util.ArrayList;
import storage.Storage;

public class DeleteCommand extends Command{

    private int id;

	public DeleteCommand(int id) {
	    this.id = id;
	}

	public int getId(){
	    return id;
	}

    public ArrayList<Task> execute(Storage storage){
        return storage.deleteTask(getId());
    }
}
