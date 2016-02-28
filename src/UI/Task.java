package application;

import java.util.Date;

public class Task {
    
    private String _description;
    private Date _start;
    private Date _end;
    private int _id;
    
    private static int num = 0;
    
    public Task() {
        _description = "Lorem ipsum dolor sit amet dolores umbridge eco llama";
        _start = new Date();
        _end = new Date();
        _id = ++num;
    }

    public String getDescription() {
        return _description;
    }

    public Date getStart() {
        return _start;
    }

    public Date getEnd() {
        return _end;
    }

    public int getID() {
        return _id;
    }

    public String getStartDate() {
        return _start.toString();
    }

    public String getEndDate() {
        return _end.toString();
    }

}
