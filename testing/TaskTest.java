import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import common.Task;

public class TaskTest {
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String STR_SMALL = "aaa";
    private static final String STR_MID = "bbb";
    private static final String STR_BIG = "ccc";
    
    @Test
    public void testSameDate() throws ParseException {
        LocalDate date = LocalDateTime.parse("01/01/2016 12:00", formatter).toLocalDate();
        
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
        t2.setEnd("01/01/2016 00:00");
        assertTrue(t2.isSameDate(date) == true);
        
        Task t3 = new Task(STR_MID);
        t3.setEnd("01/01/2016 23:59");
        assertTrue(t3.isSameDate(date) == true);
        
        Task t4 = new Task(STR_MID);
        t4.setEnd("31/12/2015 23:59");
        assertTrue(t4.isSameDate(date) == false);
        
        Task t5 = new Task(STR_MID);
        t5.setEnd("02/01/2016 00:00");
        assertTrue(t5.isSameDate(date) == false);

        Task t6 = new Task(STR_MID);
        t6.setStart("01/01/2016 00:00");
        t6.setEnd("31/12/2016 00:00");
        assertTrue(t6.isSameDate(date) == true);
        
        Task t7 = new Task(STR_MID);
        t7.setStart("31/12/2015 23:59");
        t7.setEnd("01/01/2016 23:59");
        assertTrue(t6.isSameDate(date) == true);
    }
    
    @Test
    public void testFloating() throws ParseException {
        LocalDateTime date = LocalDateTime.parse("01/04/2016 12:00", formatter);

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
        LocalDateTime smallDate = LocalDateTime.parse("01/04/2016 12:00", formatter);
        LocalDateTime midDate = LocalDateTime.parse("07/07/2016 12:00", formatter);
        LocalDateTime bigDate = LocalDateTime.parse("31/12/2016 12:00", formatter);

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
