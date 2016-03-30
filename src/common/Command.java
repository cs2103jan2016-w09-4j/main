package common;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Command {

    public enum CommandType {
        ADD, DELETE, EDIT, SEARCH, SAVE, LOAD, UNDO, REDO, HOME, DONE, SEARCHDONE, HELP, EXIT, INVALID;
    }

    private CommandType type;

    private String description;
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime searchDate;
    private ArrayList<String> categories;

    public Command(CommandType type) {
        this.type = type;
    }

    public Command(CommandType type, String description, LocalDateTime start, LocalDateTime end){ // add
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public Command(CommandType type, String description) {  // add, search
        this.type = type;
        this.description = description;
    }

    // search by date
    public Command(CommandType type, LocalDateTime date) {
    	this.type = type;
    	this.searchDate = date;
    }

    // search by categories
    public Command(CommandType type, ArrayList<String> categories) {
    	this.type = type;
    	this.categories = categories;
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

    public Command(CommandType type, int id, String description,  LocalDateTime start, LocalDateTime end) { // edit
        this.type = type;
        this.id = id;
        this.description = description;
        this.start = start;
        this.end = end;
    }

    public Command(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object x) {
        if (!(x instanceof Command))
            return false;

        Command xCommand = (Command) x;
        if (type != xCommand.type) return false;
        if (id != xCommand.id) return false;
        if (!start.equals(xCommand.start)) return false;
        if (!end.equals(xCommand.end)) return false;
        if (!description.equals(xCommand.description)) return false;
        if (!categories.equals(xCommand.categories)) return false;

        return true;
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

    public LocalDateTime getSearchDate() {
    	return searchDate;
    }

    public ArrayList<String> getCategories() {
    	return categories;
    }

}
