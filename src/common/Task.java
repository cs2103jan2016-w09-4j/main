package common;

import java.util.Date;

public class Task {
    
    private String description;
    private Date start;
    private Date end;
    private int id;
    
    // for placeholder id
    private static int num = 0;
	
	public Task(String description) {
		this.description = description;
		// use placeholder dates and id for now
        start = new Date();
        end = new Date();
        id = ++num;
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