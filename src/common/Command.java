package common;

public class Command {
    public enum CommandType {
        ADD, DELETE, EDIT, SEARCH, INVALID;
    };

    CommandType type;

    private int id;
    private String description;

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

    public Command(String description){
    	this.description = description;
    }

    public CommandType getType() {
        return type;
    }

    public String getDescription() {
        return this.description;
    }

    public int getId() {
        return id;
    }
}
