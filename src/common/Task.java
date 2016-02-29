package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    
    private String description;
    private Date start;
    private Date end;
    private int id;
    
    // TODO: replace placeholder id
    private static int num = 0;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	public Task(String description) {
		this.description = description;
		
		// TODO: replace placeholder dates and id
		id = ++num;
		try {
            if (id%2 == 1) {
                start = dateFormat.parse("29-02-2016");
                end = dateFormat.parse("29-02-2016");
            } else {
                start = dateFormat.parse("01-04-2016");
                end = dateFormat.parse("01-04-2016");
            }
        } catch (ParseException e) {
            start = new Date();
            end = new Date();
        }
	}
	
	public void setDescription(String line) {
		description = line;
	}
	
	public String getDescription() {
		return description;
	}
	
    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public int getID() {
        return id;
    }

    public String getStartDate() {
        return start.toString();
    }

    public String getEndDate() {
        return end.toString();
    }

}	