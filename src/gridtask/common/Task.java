//@@author A0131507R
package gridtask.common;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Represents a single task with task details.
 *
 * Floating task - no end date
 * Deadline task - has end date only
 * Event task - has start and end dates
 */
public class Task implements Comparable<Task> {

    // Task details
    private String description;
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ArrayList<String> categories;
    private boolean isImportant;
    private boolean isModified;
    
    // Return values for compareTo method defined in Comparable interface
    private static final int GREATER_THAN = 1;
    private static final int LESS_THAN = -1;

    // For formatting start and end dates/times of different task types
    private static final String TASK_DETAILS_DATE_FLOATING = "From %s";
    private static final String TASK_DETAILS_DATE_DEADLINE = "By %s";
    private static final String TASK_DETAILS_DATE_EVENT = "From %s to %s";
    private static final String TASK_DETAILS_DATE_EVENT_ONE_DAY = "On %s, from %s to %s";
    
    // For formatting the String representation of a Task
    private static final String TASK_STRING = "%s|%s|%s|%s|%s|%s";
    private static final String TASK_STRING_NO_ID = "%s|%s|%s|%s|%s";
    
    // For formatting a LocalDateTime into a String
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Class constructor specifying the task description
     */
    public Task(String description) {
        this(description, null, null, 0);
    }
    
    /**
     * Class constructor specifying the task description and id
     */
    public Task(String description, int id) {
        this(description, null, null, id);
    }
    
    /**
     * Class constructor specifying the task description and date/time
     */
    public Task(String description, LocalDateTime start, LocalDateTime end) {
        this(description, start, end, 0);
    }
    
    /**
     * Class constructor specifying the task description, date/time and id
     */
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
    
    public LocalDateTime getStart() {
        return start;
    }
    
    public LocalDate getStartDate() {
        if (start != null) {
            return start.toLocalDate();
        }
        return null;
    }
    
    /**
     * Returns the start date/time formatted as dd/MM/yyyy HH:mm.
     * A task without a start date/time will return an empty String.
     * 
     * @return          the formatted start date
     */
    public String getStartString() {
        if (start != null) {
            return formatDateTime(start);
        }
        return "";
    }
    
    /**
     * Returns the start date/time formatted as dd/MM/yyyy HH:mm, relative to the given date.
     * If the start date is the same the given date, returns only HH:mm.
     * A task without a start date/time will return an empty String.
     * 
     * @return          the formatted start date
     */
    public String getRelativeStartString(LocalDate now) {
        if (start != null) {
            return formatRelativeDateTime(start, now);
        }
        return "";
    }

    public LocalDateTime getEnd() {
        return end;
    }
    
    public LocalDate getEndDate() {
        if (end != null) {
            return end.toLocalDate();
        }
        return null;
    }
    
    /**
     * Returns the end date/time formatted as dd/MM/yyyy HH:mm.
     * A floating task will return an empty String.
     * 
     * @return          the formatted end date
     */
    public String getEndString() {
        if (end != null) {
            return formatDateTime(end);
        }
        return "";
    }
    
    /**
     * Returns the end date/time formatted as dd/MM/yyyy HH:mm, relative to the given date.
     * If the end date is the same the given date, returns only HH:mm.
     * A floating task will return an empty String.
     * 
     * @return          the formatted end date
     */
    public String getRelativeEndString(LocalDate now) {
        if (end != null) {
            return formatRelativeDateTime(end, now);
        }
        return "";
    }
    
    /**
     * Returns the start, end date/time formatted based on the task type.
     * For example, an event is formatted as "From dd/MM/yyyy HH:mm to dd/MM/yyy HH:mm".
     * 
     * @return          the formatted start and end date
     */
    public String getStartEndString() {
        String startEndString = "";
        if (isFloating()) {
            if (start != null) {
                String startString = getStartString();
                startEndString = String.format(TASK_DETAILS_DATE_FLOATING, startString);
            }
        } else if (isDeadline()) {
            String endString = getEndString();
            startEndString = String.format(TASK_DETAILS_DATE_DEADLINE, endString);
        } else if (isEvent()) {
            String startString = getStartString();
            String endString = getEndString();
            startEndString = String.format(TASK_DETAILS_DATE_EVENT, startString, endString);
        }
        return startEndString;
    }
    
    /**
     * Returns the start, end date/time formatted based on the task type, relative to the given date.
     * For example, an event occurring on the same day is formatted as "From HH:mm to HH:mm".
     * 
     * @return          the formatted start and end date
     */
    public String getRelativeStartEndString(LocalDate now) {
        String startEndString = "";
        if (isFloating()) {
            if (start != null) {
                String startString = getRelativeStartString(now);
                startEndString = String.format(TASK_DETAILS_DATE_FLOATING, startString);
            }
        } else if (isDeadline()) {
            String endString = getRelativeEndString(now);
            startEndString = String.format(TASK_DETAILS_DATE_DEADLINE, endString);
        } else if (isEvent()) {
            LocalDate startDate = getStartDate();
            LocalDate endDate = getEndDate();
            if (startDate.isEqual(endDate) && !startDate.isEqual(now)) {
                String startString = getRelativeStartString(startDate);
                String endString = getRelativeEndString(endDate);
                String date = dateFormatter.format(start);
                startEndString = String.format(TASK_DETAILS_DATE_EVENT_ONE_DAY, date, startString, endString);
            } else {
                String startString = formatRelativeDateTime(start, now);
                String endString = formatRelativeDateTime(end, now);
                startEndString = String.format(TASK_DETAILS_DATE_EVENT, startString, endString);
            }
        }
        return startEndString;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }
    
    /**
     * Returns the categories associated with this task in a formatted String.
     * 
     * @return          the formatted String of categories
     */
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
    
    public void setImportant(boolean isImportant) {
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
    
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTimeFormatter.format(dateTime);
    }
    
    private String formatRelativeDateTime(LocalDateTime dateTime, LocalDate now) {
        if (dateTime.toLocalDate().isEqual(now)) {
            return timeFormatter.format(dateTime);
        } else {
            return dateTimeFormatter.format(dateTime);
        }
    }

}
