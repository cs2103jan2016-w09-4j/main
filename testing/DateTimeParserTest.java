import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import net.fortuna.ical4j.model.TimeZone;
import parser.DateTimeParser;

public class DateTimeParserTest {

    @Test
    public void parse_GeneralTest() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 10, 20), LocalTime.of(10, 59)), parser.parse("add eat start 10:59 20-10-2016 good", true));
    }

    @Test
    public void parse_HasEndTime(){
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)), parser.parse("end 9:10 29-8-2004", true));
    }

    @Test
    public void parse_HasTimeAndNoYear_ReturnTimeWithCurrentYear(){
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 8, 29), LocalTime.of(9, 10)), parser.parse("end 9:10 29-8", true));
    }

    @Test
    public void parse_HasTimeAndNoDate_ReturnTimeWithCurrentDate(){
    	Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    	Date date = calendar.getTime();
    	int day = calendar.get(Calendar.DATE);
    	int month = calendar.get(Calendar.MONTH) + 1;
    	int year = calendar.get(Calendar.YEAR);
    	
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(year, month, day), LocalTime.of(17, 0)), parser.parse("add task 5pm", true));
    }

}
