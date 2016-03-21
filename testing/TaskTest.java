import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import common.Task;

public class TaskTest {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final String STR_SMALL = "aaa";
    private static final String STR_MID = "bbb";
    private static final String STR_BIG = "ccc";
    
    @Test
    public void testSameDate() throws ParseException {
        Date date = dateFormat.parse("1/1/2016 12:00");
        
        /*
         * Equivalence partition:
         * Start and end dates can take up the possible values
         * [between "1/1/2016 00:00" and "1/1/2016 23:59"]
         * [<= "31/12/2015 23:59"] [>= "2/1/2016 00:00"] [null]
         * 
         */
        
        Task t1 = new Task(STR_MID);
        assertTrue(t1.isSameDate(date) == false);
        
        Task t2 = new Task(STR_MID);
        t2.setEnd("1/1/2016 00:00");
        assertTrue(t2.isSameDate(date) == true);
        
        Task t3 = new Task(STR_MID);
        t3.setEnd("1/1/2016 23:59");
        assertTrue(t3.isSameDate(date) == true);
        
        Task t4 = new Task(STR_MID);
        t4.setEnd("31/12/2015 23:59");
        assertTrue(t4.isSameDate(date) == false);
        
        Task t5 = new Task(STR_MID);
        t5.setEnd("2/1/2016 00:00");
        assertTrue(t5.isSameDate(date) == false);

        Task t6 = new Task(STR_MID);
        t6.setStart("1/1/2016 00:00");
        t6.setEnd("31/12/2016 00:00");
        assertTrue(t6.isSameDate(date) == true);
        
        Task t7 = new Task(STR_MID);
        t7.setStart("31/12/2015 23:59");
        t7.setEnd("1/1/2016 23:59");
        assertTrue(t6.isSameDate(date) == true);
    }
    
    @Test
    public void testFloating() throws ParseException {
        final Date date = dateFormat.parse("1/4/2016 12:00");
        
        Task t1 = new Task(STR_MID);
        Task t2 = new Task(STR_MID);
        
        assertTrue("t2 is floating with equal desc", t1.compareTo(t2) == 0);
        
        Task t3 = new Task(STR_BIG);
        assertTrue("t3 is floating with greater desc", t1.compareTo(t3) < 0);
        t3.setDescription(STR_SMALL);
        assertTrue("t3 is floating with smaller desc", t1.compareTo(t3) > 0);
        
        Task t4 = new Task(STR_SMALL);
        t4.setEnd(date);
        assertTrue("t4 is non-floating", t1.compareTo(t4) > 0);
    }
    
    @Test
    public void testDeadline() throws ParseException {
        final Date smallDate = dateFormat.parse("1/4/2016 12:00");
        final Date midDate = dateFormat.parse("7/7/2016 12:00");
        final Date bigDate = dateFormat.parse("31/12/2016 12:00");

        Task t1 = new Task(STR_MID);
        t1.setEnd(midDate);
        
        Task t2 = new Task(STR_MID);
        assertTrue("t2 is floating", t1.compareTo(t2) < 0);
        
        Task t3 = new Task(STR_MID);
        t3.setEnd(midDate);
        assertTrue("t3 is deadline with same date, desc", t1.compareTo(t3) == 0);
        t3.setDescription(STR_SMALL);
        assertTrue("t3 is deadline with same date, diff desc", t1.compareTo(t3) != 0);
        
        Task t4 = new Task(STR_MID);
        t4.setStart(midDate);
        t4.setEnd(midDate);
        assertTrue("t4 is event with same end date, desc", t1.compareTo(t4) == 0);
        t4.setDescription(STR_BIG);
        assertTrue("t4 is event with same end date, diff desc", t1.compareTo(t4) != 0);
    }
    
    @Test
    public void testEvent() throws ParseException {
        
    }
    
}
