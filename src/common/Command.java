package common;

import java.util.ArrayList;

import common.Result;
import common.Task;
import storage.Storage;

public class Command {
    
    public static final int ADD_TYPE = 1;
    public static final int DELETE_TYPE = 2;
    public static final int EDIT_TYPE = 3;
    public static final int SEARCH_TYPE = 4;
    public static final int INVALID_TYPE = -1;
    private int type;
    private int id;
    private String description;
    private String editedDescription;
    private String searchWord;
    
    
    public Command(int type) {
        this.type = type;
    }

    public Command(int type, String description) {  // add
        this.type = type;
        this.description = description;
    }

    public Command(int type, int id) { // delete 
        this.type = type;
        this.id = id;
    }

    public Command(int type, int id, String description) { // edit
        this.type = type;
        this.id = id;
        this.editedDescription = description;
    }
    
    public Command(String searchWord){
    	this.searchWord = searchWord;
    }

    public int getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public int getId() {
        return this.id;
    }
    
    public String getEditedDescription(){
    	return this.editedDescription;
    }

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
    }
}
