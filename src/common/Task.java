package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Task {
    
    private String description;
    private int id;
    private Date start;
    private Date end;
    
    // TODO: replace placeholder id and categories
    private static int num = 0;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private ArrayList<String> categories;
	
	public Task(String description) {
		this.description = description;
		
		// TODO: replace placeholder dates and id and categories
		id = ++num;
		try {
            if (id%2 == 1) {
                start = new Date();
                end = new Date();
            } else {
                start = dateFormat.parse("31-12-2016");
                end = dateFormat.parse("31-12-2016");
            }
        } catch (ParseException e) {
            start = new Date();
            end = new Date();
        }
		categories = new ArrayList<String>(Arrays.asList("Priority", "Today", "School"));
	}
	
	/******************
	 * SETTER METHODS *
	 ******************/
	
	public void setDescription(String line) {
		description = line;
	}
	
    public void setID(int id) {
        this.id = id;
    }
        
    public void setStart(Date date) {
        start = date;
    }

    public void setEnd(Date date) {
        end = date;
    }
    
    /******************
     * GETTER METHODS *
     ******************/
    
	public String getDescription() {
		return description;
	}

    public int getID() {
        return id;
    }
    	
    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
    
    public ArrayList<String> getCategories() {
        return categories;
    }

    /******************
     * HELPER METHODS *
     ******************/

    public boolean isToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(start).equals(dateFormat.format(new Date()));
    }

}	