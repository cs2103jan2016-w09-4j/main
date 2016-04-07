package common;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.scene.control.Label;

public class Task implements Comparable<Task> {
    
    private static final int LESS_THAN = -1;
    private static final int GREATER_THAN = 1;
    private static final String TASK_STRING = "%s|%s|%s|%s|%s|%s";
    private static final String TASK_STRING_NO_ID = "%s|%s|%s|%s|%s";
    private static final String TASK_DETAILS_DATE_FLOATING = "From %s";
    private static final String TASK_DETAILS_DATE_DEADLINE = "By %s";
    private static final String TASK_DETAILS_DATE_EVENT = "From %s to %s";
    private static final String TASK_DETAILS_DATE_EVENT_ONE_DAY = "On %s, from %s to %s";

    private String description;
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ArrayList<String> categories;
    private boolean isImportant;
    private boolean isModified;
    
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public Task(String description) {
        this(description, null, null, 0);
    }
    
    public Task(String description, int id) {
        this(description, null, null, id);
    }
    
    public Task(String description, LocalDateTime start, LocalDateTime end) {
        this(description, start, end, 0);
    }
    
    public Task(String description, LocalDateTime start, LocalDateTime end, int id) {
        this.description = description;
        this.start = start;
        this.end = end;
        this.id = id;
        categories = new ArrayList<String>();
        isImportant = false;
        isModified = false;
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
	   if (start != null) {
		   return dateTimeFormatter.format(start);
	   } 
	   
	   String start2 = "";
	   return start2;
    }

    public LocalDateTime getEndDate() {
        return end;
    }
    
    public String getEndDateString() {
        //return formatter.format(end);
    	if (end != null) {
    		return dateTimeFormatter.format(end);
 	   } 
 	   
 	   String end2 = "";
 	   return end2;
    }
    
    public String getStartEndString() {
        String startEndDate = "";
        if (isFloating()) {
            if (start != null) {
                String startDate = formatDate(start);
                startEndDate = String.format(TASK_DETAILS_DATE_FLOATING, startDate);
            }
        } else if (isDeadline()) {
            String endDate = formatDate(end);
            startEndDate = String.format(TASK_DETAILS_DATE_DEADLINE, endDate);
        } else if (isEvent()) {
            String startDate = formatDate(start);
            String endDate = formatDate(end);
            startEndDate = String.format(TASK_DETAILS_DATE_EVENT, startDate, endDate);
        }
        return startEndDate;
    }
    
    public String getRelativeStartEndString(LocalDate now) {
        String startEndDate = "";
        if (isFloating()) {
            if (start != null) {
                String startDate = formatRelativeDate(start, now);
                startEndDate = String.format(TASK_DETAILS_DATE_FLOATING, startDate);
            }
        } else if (isDeadline()) {
            String endDate = formatRelativeDate(end, now);
            startEndDate = String.format(TASK_DETAILS_DATE_DEADLINE, endDate);
        } else if (isEvent()) {
            LocalDate startD = start.toLocalDate();
            LocalDate endD = end.toLocalDate();
            if (startD.isEqual(endD) && !startD.isEqual(now)) {
                String startDate = formatRelativeDate(start, startD);
                String endDate = formatRelativeDate(end, endD);
                startEndDate = String.format(TASK_DETAILS_DATE_EVENT_ONE_DAY, startD, startDate, endDate);
            } else {
                String startDate = formatRelativeDate(start, now);
                String endDate = formatRelativeDate(end, now);
                startEndDate = String.format(TASK_DETAILS_DATE_EVENT, startDate, endDate);
            }
        }
        return startEndDate;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }
    
    public String getCategoriesString() {
        String allCategories = "";
        for (String cat : categories) {
            allCategories += " #" + cat;
        }
        return allCategories.trim();
    }

    public boolean isImportant() {
        return isImportant;
    }
    
    public boolean isModified() {
        return isModified;
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
        LocalDateTime startDate = LocalDateTime.parse(date, dateTimeFormatter);
        this.start = startDate;
    }

    public void setEnd(LocalDateTime date) {
        end = date;
    }
    
    public void setEnd(String date) throws ParseException {
        LocalDateTime endDate = LocalDateTime.parse(date, dateTimeFormatter);
        this.end = endDate;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
    
    public void setImportance(boolean isImportant) {
        this.isImportant = isImportant;
    }
        
    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }
    
    /******************
     * HELPER METHODS *
     ******************/
    
    /**
     * Checks if this task is overdue relative to the given date and time.
     * Returns true if and only if this task ends before the date and time.
     * If this task is a floating task, returns false.
     * 
     * @param dateTime  date and time for comparison 
     * @return          true if end is earlier than dateTime
     */
    public boolean isOverdue(LocalDateTime dateTime) {
        if (isEvent() || isDeadline()) {
            return end.isBefore(dateTime);
        }
        return false;
    }
    
    /**
     * Returns true if this task occurs on the given date.
     * 
     * @param date      date for comparison
     * @return          true if given date falls between start or end
     */
    public boolean isOccurringOn(LocalDate date) {
        if (isFloating()) {
            if (start != null) {
                LocalDate startDate = start.toLocalDate();
                return startDate.isEqual(date);
            }
            return false;
        } else if (isEvent()) {
            LocalDate startDate = start.toLocalDate();
            LocalDate endDate = end.toLocalDate();
            boolean isBefore = date.isBefore(startDate);
            boolean isAfter = date.isAfter(endDate);
            return !isBefore && !isAfter;
        } else if (isDeadline()) {
            LocalDate endDate = end.toLocalDate();
            return endDate.isEqual(date);
        }
        return false;
    }
    
    /**
     * Compares this task to another task.
     * Comparison is mainly based on the start and end date/time. If the
     * dates are equal, then the task description is used for comparison.
     * Use to facilitate sorting methods.
     * 
     * @param t2        the other task to compare to
     * @return          0 if the tasks are equal, a negative integer if
     *                  this task is less than the specified task, and a
     *                  positive integer if this task is greater than the
     *                  specified task
     */
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
            int endComparison = this.end.compareTo(t2.end);
            if (endComparison == 0) {
                if (t2.isDeadline()) {
                    return this.description.compareTo(t2.description);
                }
                int startComparison = this.start.compareTo(t2.start);
                if (startComparison == 0) {
                    return this.description.compareTo(t2.description);
                }
                return startComparison;
            }
            return endComparison; 
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

    /**
     * Returns true if this task is a floating task.
     * A floating task is a task that does not have a start or end date.
     * 
     * @return          true if task is floating
     */
    public boolean isFloating() {
        boolean hasEnd = this.end != null;
        return !hasEnd;
    }
    
    /**
     * Returns true if this task is an event.
     * An event is a task that takes place within a specified period,
     * i.e. has a start and end date.
     * 
     * @return          true if task is an event
     */
    public boolean isEvent() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return hasStart && hasEnd;
    }
    
    /**
     * Returns true if this task is a deadline.
     * A deadline is a task that has an end date, but no start date.
     * 
     * @return          true if task is a deadline
     */
    public boolean isDeadline() {
        boolean hasStart = this.start != null;
        boolean hasEnd = this.end != null;
        return !hasStart && hasEnd;
    }
    
    @Override
    /**
     * Represents this task as a String.
     * 
     * @return          a String representation of this task, not null
     */
    public String toString() {
        String startStr = null;
        if (start != null) {
            startStr = dateTimeFormatter.format(start);
        }
        String endStr = null;
        if (end != null) {
            endStr = dateTimeFormatter.format(end);
        }
        String catStr = "";
        for (String cat : categories) {
            catStr += "#" + cat;
        }
        String task = String.format(TASK_STRING, description, id,
                startStr, endStr, catStr.isEmpty() ? null : catStr, isImportant);
        return task;
    }
    
    public String toStringIgnoreId() {
        String startStr = null;
        if (start != null) {
            startStr = dateTimeFormatter.format(start);
        }
        String endStr = null;
        if (end != null) {
            endStr = dateTimeFormatter.format(end);
        }
        String catStr = "";
        for (String cat : categories) {
            catStr += "#" + cat;
        }
        String task = String.format(TASK_STRING_NO_ID, description,
                startStr, endStr, catStr.isEmpty() ? null : catStr, isImportant);
        return task;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Task) {
            Task t2 = (Task) o;
            return this.toString().equals(t2.toString());
        }
        return false;
    }
    
    private String formatDate(LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime);
    }
    
    private String formatRelativeDate(LocalDateTime dateTime, LocalDate now) {
        if (dateTime.toLocalDate().isEqual(now)) {
            return timeFormatter.format(dateTime);
        } else {
            return dateTimeFormatter.format(dateTime);
        }
    }

}
