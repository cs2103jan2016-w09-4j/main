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
	
        
    public void setStart(Date date) {
        start = date;
    }
    
    /*public void setStartDateAndTime(String date) throws ParseException {
    	Date startDate = dateFormat.parse(date);
    	this.start = startDate;
    }*/

    public void setEnd(Date date) {
        end = date;
    }
    
   /* public void setEndDateAndTime(String date) throws ParseException {
    	Date endDate = dateFormat.parse(date);
    	this.end = endDate;
    }*/

   
    public void setCategory(String categoryName) {
    	categories.add(categoryName);
    }
    
    
    /******************
     * GETTER METHODS *
     ******************/
    
	public String getDescription() {
		return description;
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
     * HELPER METHODS *
     ******************/

    public boolean isToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(start).equals(dateFormat.format(new Date()));
    }

}	