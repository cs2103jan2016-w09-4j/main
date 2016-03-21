package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Task implements Comparable<Task> {
    
    private static final int LESS_THAN = -1;
    private static final int GREATER_THAN = 1;
    
    private String description;
    private int id;
    private Date start;
    private Date end;
    private ArrayList<String> categories;
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public Task(String description) {
		this.description = description;
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
    	
    public Date getStartDate() {
        return start;
    }
    
   /*public String getStartDateString() {
    	return dateFormat.format(start);
    }*/

    public Date getEndDate() {
        return end;
    }
    
    /*public String getEndDateString() {
    	return dateFormat.format(end);
    }*/

    
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
        
    public void setStart(Date date) {
        start = date;
    }
    
    public void setStart(String date) throws ParseException {
        Date startDate = dateFormat.parse(date);
        this.start = startDate;
    }

    public void setEnd(Date date) {
        end = date;
    }
    
    public void setEnd(String date) throws ParseException {
        Date endDate = dateFormat.parse(date);
        this.end = endDate;
    }

    public void setCategory(String categoryName) {
        categories.add(categoryName);
    }
    
    /******************
     * HELPER METHODS *
     ******************/

    public boolean isSameDate(Date date) {
        String dateStr = dateFormat.format(date);
        if (isFloating()) {
            return false;
        } else if (isEvent()) {
            return dateFormat.format(start).equals(dateStr) || dateFormat.format(end).equals(dateStr);
        } else if (isDeadline()) {
            return dateFormat.format(end).equals(dateStr);
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
    private boolean isFloating() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return !hasStart && !hasEnd;
    }
    
    // Returns true if task is an event with a start and end
    private boolean isEvent() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return hasStart && hasEnd;
    }
    
    // Returns true if task only has a deadline
    private boolean isDeadline() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return !hasStart && hasEnd;
    }

}	