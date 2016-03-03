package common;

import java.util.ArrayList;
<<<<<<< HEAD

import common.Result;
import common.Task;
=======
>>>>>>> try_stuff
import storage.Storage;

public class Command {
    public Command(){
    }

<<<<<<< HEAD
    public Result execute(Command command, Storage storage) {
        // TODO: this is just a stub to return a usable result
    	
    	int commandType = command.getType();
    	int commandID = command.getId();
    	String commandDescription = command.getDescription();
    	ArrayList<Task> list = new ArrayList<Task>();
    	
    	// how do i extract out specific info from the Command object?
    	
    	switch(commandType){
    		
    		case ADD_TYPE:
    			AddCommand add = new AddCommand();
    			list = add.execute(commandDescription, storage);
    			break;
    			
    		case DELETE_TYPE:
    			DeleteCommand delete = new DeleteCommand();
    			list = delete.execute(commandID, storage);
    			break;
    		
    		case EDIT_TYPE:
    			EditCommand edit = new EditCommand();
    			list = edit.execute(commandID, editedDescription, storage);
    			break;
    			
    		case SEARCH_TYPE:
    			SearchCommand search = new SearchCommand();
    			list = search.execute(searchWord, storage);
    			break;
    			
    		default:
    			list.add(new Task("Invalid input!\n"));
    			return new Result(); // invalid 
    		}
    	
    		Result result = new Result(true, "Success!", list);  // valid
    		return result;
=======
    public ArrayList<Task> execute(Storage storage){
        return new ArrayList<Task>();  // dummy
>>>>>>> try_stuff
    }
}
