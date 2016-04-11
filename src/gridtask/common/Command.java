package gridtask.common;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents the command entered by the user.
 * Contains several fields such as the command type and command parameters.
 */
public class Command {

    // Types of commands available
    public enum CommandType {
        ADD, DELETE, EDIT, DONE, UNDO, REDO, SAVE, LOAD,
        SEARCH, SEARCHDONE, HOME, HELP, EXIT, INVALID;
    }

    // Command type
    private CommandType type;

    // Command parameters entered by the user
    private String description;
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ArrayList<String> categories;

    public Command(CommandType type) {
        this.type = type;
    }

    /**
     * Class constructor specifying the command type and task description.
     */
    public Command(CommandType type, String description) {
        this.type = type;
        this.description = description;
    }

    /**
     * Class constructor specifying the command type and all task details.
     */
    public Command(CommandType type, String description,
            LocalDateTime start, LocalDateTime end, ArrayList<String> categories) {
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.categories = categories;
    }

    /**
     * Class constructor specifying the command type and task id.
     */
    public Command(CommandType type, int id) { // delete
        this.type = type;
        this.id = id;
    }

    /**
     * Class constructor specifying the command type, task id and task description.
     */
    public Command(CommandType type, int id, String description) {
        this.type = type;
        this.id = id;
        this.description = description;
    }

    /**
     * Class constructor specifying the command type, task id and all task details.
     */
    public Command(CommandType type, int id, String description,
            LocalDateTime start, LocalDateTime end, ArrayList<String> categories) {
    	  this.type = type;
          this.id = id;
          this.description = description;
          this.start = start;
          this.end = end;
          this.categories = categories;
	}

	@Override
    public boolean equals(Object o) {
        if (o instanceof Command) {
            Command c2 = (Command) o;
            if (type != c2.type) {
                return false;
            }
            if (id != c2.id) {
                return false;
            }
            if (start == null ? c2.start != null : !start.equals(c2.start)) {
                return false;
            }
            if (end == null ? c2.end != null : !end.equals(c2.end)) {
                return false;
            }
            if (description == null ? c2.description != null : !description.equals(c2.description)) {
                return false;
            }
            if (categories == null ? c2.categories!=null : !categories.equals(c2.categories)) {
                return false;
            }
            return true;
        }
        return false;
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

    public LocalDateTime getStartDate(){
        return start;
    }

    public LocalDateTime getEndDate(){
        return end;
    }

    public ArrayList<String> getCategories() {
    	return categories;
    }

}
