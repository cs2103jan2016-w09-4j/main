import static org.junit.Assert.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import common.Task;

public class TaskTest {
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String DATE_FIRST = "01/04/2016 12:00";
    private static final String DATE_SECOND = "07/07/2016 12:00";
    private static final String DATE_THIRD = "31/12/2016 12:00";
    private static final String TASK_DESCRIPTION_FIRST = "aaa";
    private static final String TASK_DESCRIPTION_SECOND = "bbb";
    private static final String TASK_DESCRIPTION_THIRD = "ccc";
    
    /*
     * Equivalence partitioning:
     * Start and end dates can take up the possible values
     * [between "1/1/2016 00:00" and "1/1/2016 23:59"],
     * [<= "31/12/2015 23:59"], [>= "2/1/2016 00:00"], [null]
     * 
     */
    @Test
    public void isSameDate_CompareGivenDate_ExpectedBoolean() throws ParseException {
        LocalDate date = LocalDateTime.parse("01/01/2016 12:00", formatter).toLocalDate();

        Task floating = new Task(TASK_DESCRIPTION_SECOND);
        assertFalse(floating.isSameDate(date));
        
        Task deadlineSameEnd0000 = new Task(TASK_DESCRIPTION_SECOND);
        deadlineSameEnd0000.setEnd("01/01/2016 00:00");
        assertTrue(deadlineSameEnd0000.isSameDate(date));
        
        Task deadlineSameEnd2359 = new Task(TASK_DESCRIPTION_SECOND);
        deadlineSameEnd2359.setEnd("01/01/2016 23:59");
        assertTrue(deadlineSameEnd2359.isSameDate(date));
        
        Task deadlineDiffEnd0000 = new Task(TASK_DESCRIPTION_SECOND);
        deadlineDiffEnd0000.setEnd("02/01/2016 00:00");
        assertFalse(deadlineDiffEnd0000.isSameDate(date));
        
        Task deadlineDiffEnd2359 = new Task(TASK_DESCRIPTION_SECOND);
        deadlineDiffEnd2359.setEnd("31/12/2015 23:59");
        assertFalse(deadlineDiffEnd2359.isSameDate(date));

        Task eventSameStart = new Task(TASK_DESCRIPTION_SECOND);
        eventSameStart.setStart("01/01/2016 00:00");
        eventSameStart.setEnd("31/12/2016 00:00");
        assertTrue(eventSameStart.isSameDate(date));
        
        Task eventSameEnd = new Task(TASK_DESCRIPTION_SECOND);
        eventSameEnd.setStart("31/12/2015 23:59");
        eventSameEnd.setEnd("01/01/2016 23:59");
        assertTrue(eventSameStart.isSameDate(date));
    }
    
    @Test
    public void compareTo_FloatingTask_ExpectedReturnValue() throws ParseException {
        LocalDateTime date = LocalDateTime.parse(DATE_FIRST, formatter);
        Task floating = new Task(TASK_DESCRIPTION_SECOND);
        
        Task t2 = new Task(TASK_DESCRIPTION_SECOND);
        assertTrue("t2 is floating with same desc", floating.compareTo(t2) == 0);
        
        Task t3 = new Task(TASK_DESCRIPTION_THIRD);
        assertTrue("t3 is floating with greater desc", floating.compareTo(t3) < 0);
        t3.setDescription(TASK_DESCRIPTION_FIRST);
        assertTrue("t3 is floating with smaller desc", floating.compareTo(t3) > 0);
        
        Task t4 = new Task(TASK_DESCRIPTION_FIRST);
        t4.setEnd(date);
        assertTrue("t4 is non-floating", floating.compareTo(t4) > 0);
    }
    
    @Test
    public void compareTo_DeadlineTask_ExpectedReturnValue() throws ParseException {
        LocalDateTime firstDate = LocalDateTime.parse(DATE_FIRST, formatter);
        LocalDateTime secondDate = LocalDateTime.parse(DATE_SECOND, formatter);
        LocalDateTime thirdDate = LocalDateTime.parse(DATE_THIRD, formatter);
        Task deadline = new Task(TASK_DESCRIPTION_SECOND);
        deadline.setEnd(secondDate);
        
        Task t2 = new Task(TASK_DESCRIPTION_SECOND);
        assertTrue("t2 is floating", deadline.compareTo(t2) < 0);
        
        Task t3 = new Task(TASK_DESCRIPTION_SECOND);
        t3.setEnd(secondDate);
        assertTrue("t3 is deadline with same date, same desc", deadline.compareTo(t3) == 0);
        t3.setDescription(TASK_DESCRIPTION_FIRST);
        assertTrue("t3 is deadline with same date, diff desc", deadline.compareTo(t3) != 0);
        
        Task t4 = new Task(TASK_DESCRIPTION_SECOND);
        t4.setEnd(firstDate);
        assertTrue("t4 is deadline with smaller date, same desc", deadline.compareTo(t4) > 0);
        t4.setEnd(thirdDate);
        assertTrue("t4 is deadline with greater date, same desc", deadline.compareTo(t4) < 0);
        
        Task t5 = new Task(TASK_DESCRIPTION_SECOND);
        t5.setStart(secondDate);
        t5.setEnd(secondDate);
        assertTrue("t6 is event with same end date, same desc", deadline.compareTo(t5) == 0);
        t5.setDescription(TASK_DESCRIPTION_FIRST);
        assertTrue("t6 is event with same end date, diff desc", deadline.compareTo(t5) != 0);
    }
    
    @Test
    public void compareTo_EventTask_ExpectedReturnValue() throws ParseException {
        LocalDateTime firstDate = LocalDateTime.parse(DATE_FIRST, formatter);
        LocalDateTime secondDate = LocalDateTime.parse(DATE_SECOND, formatter);
        LocalDateTime thirdDate = LocalDateTime.parse(DATE_THIRD, formatter);
        Task event = new Task(TASK_DESCRIPTION_SECOND);
        event.setStart(secondDate);
        event.setEnd(secondDate);
        
        Task t2 = new Task(TASK_DESCRIPTION_SECOND);
        assertTrue("t2 is floating", event.compareTo(t2) < 0);
        
        Task t3 = new Task(TASK_DESCRIPTION_SECOND);
        t3.setEnd(secondDate);
        assertTrue("t3 is deadline with same end date, same desc", event.compareTo(t3) == 0);
        t3.setDescription(TASK_DESCRIPTION_FIRST);
        assertTrue("t3 is deadline with same end date, diff desc", event.compareTo(t3) != 0);
        
        Task t4 = new Task(TASK_DESCRIPTION_SECOND);
        t4.setStart(secondDate);
        t4.setEnd(secondDate);
        assertTrue("t4 is event with same start/end date, same desc", event.compareTo(t4) == 0);
        t4.setDescription(TASK_DESCRIPTION_FIRST);
        assertTrue("t4 is event with same start/end date, diff desc", event.compareTo(t4) != 0);
        
        Task t5 = new Task(TASK_DESCRIPTION_SECOND);
        t5.setStart(firstDate);
        t5.setEnd(secondDate);
        assertTrue("t5 is event with smaller start, same end", event.compareTo(t5) > 0);
        t5.setStart(secondDate);
        t5.setEnd(thirdDate);
        assertTrue("t5 is event with same start, greater end", event.compareTo(t5) < 0);
        
        Task t6 = new Task(TASK_DESCRIPTION_SECOND);
        t6.setStart(firstDate);
        t6.setEnd(firstDate);
        assertTrue("t6 is event with smaller start/end", event.compareTo(t6) > 0);
        t6.setStart(thirdDate);
        t6.setEnd(thirdDate);
        assertTrue("t6 is event with greater start/end", event.compareTo(t6) < 0);
        
        Task t7 = new Task(TASK_DESCRIPTION_SECOND);
        t7.setStart(firstDate);
        t7.setEnd(thirdDate);
        assertTrue("t7 is event with smaller start, greater end", event.compareTo(t7) < 0);
        assertTrue("t7 is event with smaller start, greater end", t7.compareTo(event) > 0);
    }
}
