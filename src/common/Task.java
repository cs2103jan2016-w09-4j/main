package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    
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
     * HELPER METHODS *
     ******************/

    public boolean isToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(start).equals(dateFormat.format(new Date()));
    }

}	