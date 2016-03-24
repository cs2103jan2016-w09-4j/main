package common;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Task implements Comparable<Task> {
    
    private static final int LESS_THAN = -1;
    private static final int GREATER_THAN = 1;
    
    private String description;
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ArrayList<String> categories;
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    
    public Task(String description) {
        this.description = description;
        categories = new ArrayList<String>();
    }

    /******************
     * GETTER METHODS *
     ******************/
    
    public String getDescription() {
        return description;
    }
    
    public int getId() {
        return id;
    }
        
    public LocalDateTime getStartDate() {
        return start;
    }
    
   public String getStartDateString() {
        return formatter.format(start);
    }

    public LocalDateTime getEndDate() {
        return end;
    }
    
    public String getEndDateString() {
        return formatter.format(end);
    }

    
    public ArrayList<String> getCategories() {
        return categories;
    }
    
    /******************
     * SETTER METHODS *
     ******************/
    
    public void setDescription(String line) {
        description = line;
    }
    
    public void setId(int index) {
        id = index;
    }
        
    public void setStart(LocalDateTime date) {
        start = date;
    }
    
    public void setStart(String date) throws ParseException {
        LocalDateTime startDate = LocalDateTime.parse(date, formatter);
        this.start = startDate;
    }

    public void setEnd(LocalDateTime date) {
        end = date;
    }
    
    public void setEnd(String date) throws ParseException {
        LocalDateTime endDate = LocalDateTime.parse(date, formatter);
        this.end = endDate;
    }

    public void setCategory(String categoryName) {
        categories.add(categoryName);
    }
    
    /******************
     * HELPER METHODS *
     ******************/

    public boolean isSameDate(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        if (isFloating()) {
            return false;
        } else if (isEvent()) {
            LocalDate startDate = start.toLocalDate();
            LocalDate endDate = end.toLocalDate();
            return startDate.equals(date) || endDate.equals(date);
        } else if (isDeadline()) {
            LocalDate endDate = end.toLocalDate();
            return endDate.equals(date);
        }
        return false;
    }

    @Override
    public int compareTo(Task t2) {
        if (t2 == null) {
            throw new NullPointerException();
        }
        if (this.isFloating()) {
            return compareFloatingTo(t2);
        } else if (this.isDeadline()) {
            return compareDeadlineTo(t2);
        } else if (this.isEvent()) {
            return compareEventTo(t2);
        }
        return this.description.compareTo(t2.description);
    }
    
    private int compareEventTo(Task t2) {
        if (t2.isFloating()) {
            return LESS_THAN;
        } else {
            int compare = this.end.compareTo(t2.end);
            if (compare == 0) {
                return this.description.compareTo(t2.description);
            }
            return compare; 
        }
    }

    private int compareDeadlineTo(Task t2) {
        if (t2.isFloating()) {
            return LESS_THAN;
        } else {
            int compare = this.end.compareTo(t2.end);
            if (compare == 0) {
                return this.description.compareTo(t2.description);
            }
            return compare;
        }
    }

    private int compareFloatingTo(Task t2) {
        if (t2.isFloating()) {
            // order lexicographically
            return this.description.compareTo(t2.description);
        } else {
            return GREATER_THAN;
        }
    }

    // Returns true if task is floating
    public boolean isFloating() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return !hasStart && !hasEnd;
    }
    
    // Returns true if task is an event with a start and end
    public boolean isEvent() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return hasStart && hasEnd;
    }
    
    // Returns true if task only has a deadline
    public boolean isDeadline() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return !hasStart && hasEnd;
    }
    
    @Override
    public String toString() {
        String str = description + "/" + start + "/" + end;
        return str;
    }

}
