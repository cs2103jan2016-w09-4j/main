package common;

import java.util.ArrayList;
import java.util.Date;

public class Command {
    
    public enum CommandType {
        ADD, DELETE, EDIT, SEARCH, SAVE, LOAD, UNDO, REDO, HOME, COMPLETE, DONE, EXIT, INVALID;
    }

    CommandType type;

    private String description;
    private int id;
    private Date start;
    private Date end;
    private ArrayList<String> categories;

    public Command(CommandType type) {
        this.type = type;
    }

    public Command(CommandType type, String description) {  // add, search
        this.type = type;
        this.description = description;
    }

    public Command(CommandType type, int id) { // delete
        this.type = type;
        this.id = id;
    }

    public Command(CommandType type, int id, String description) { // edit
        this.type = type;
        this.id = id;
        this.description = description;
    }

    public Command(String description) {
        this.description = description;
    }
    
    /******************
     * GETTER METHODS *
     ******************/

    public CommandType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
    
}
