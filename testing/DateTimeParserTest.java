//@@author Khanh
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import gridtask.parser.DateTimeParser;
import net.fortuna.ical4j.model.TimeZone;

public class DateTimeParserTest {

    @Test
    public void parse_FullTimeFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 10, 20), LocalTime.of(10, 59)), parser.parse("10:59 20-10-2016 good", true));
    }

    @Test
    public void parse_NoLeadingZeroesFormat(){
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)), parser.parse("9:10 29-8-2004", true));
    }

    @Test
    public void parse_HasTimeAndNoYear_ReturnTimeWithCurrentYear(){
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 8, 29), LocalTime.of(9, 10)), parser.parse("9:10 29-8", true));
    }

    @Test
    public void parse_HasTimeAndNoDate_ReturnTimeWithCurrentDate(){
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)), parser.parse("5pm", true));
    }
}
