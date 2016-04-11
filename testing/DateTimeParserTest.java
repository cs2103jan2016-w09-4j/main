//@@author Khanh
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Test;

import gridtask.parser.DateTimeParser;

public class DateTimeParserTest {

    @Test
    public void parse_FullTimeFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 10, 20), LocalTime.of(10, 59)), parser.parse("10:59 20-10-2016 good", true));
    }

    @Test
    public void parse_NoLeadingZeroesFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)), parser.parse("9:10 29-8-2004", true));
    }

    @Test
    public void parse_YearInTwoDigitFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2017, 8, 29), LocalTime.of(0, 0)), parser.parse("29-08-17", false));
    }


    @Test
    public void parse_MonthInWord() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(2020, 5, 29), LocalTime.of(23, 59)), parser.parse("29 May 2020", true));
        assertEquals(LocalDateTime.of(LocalDate.of(2016, 11, 15), LocalTime.of(23, 59)), parser.parse("15 November", true));
    }

    @Test
    public void parse_HasTimeAndNoYear_ReturnTimeWithCurrentYear() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.of(LocalDate.now().getYear(), 8, 29),
                                      LocalTime.of(9, 10)),
                     parser.parse("9:10 29-8", true));
    }

    @Test
    public void parse_HasTimeAndNoDate_ReturnTimeWithCurrentDate() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)), parser.parse("5pm", true));
    }

    @Test
    public void parse_TommorowRelativeDateFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1), parser.parse("tomorrow", false));
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1), parser.parse("tmr", false));
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusDays(1), parser.parse("next day", false));
    }

    @Test
    public void parse_NextMonthRelativeDateFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).plusMonths(1), parser.parse("next month", false));
    }

    @Test
    public void parse_BoundaryCaseTime() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.parse("0:0", false));
        assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59)), parser.parse("23:59", false));
    }

    @Test
    public void parse_InvalidInput_ReturnNull() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(null, parser.parse("xyz", false));
    }

    @Test
    public void parse_MultipleCombinedComplexDateTimeFormat() {
        DateTimeParser parser = new DateTimeParser();
        assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(17, 30)), parser.parse("tmr 5:30pm", true));
        assertEquals(LocalDateTime.of(LocalDate.of(2020, 4, 12), LocalTime.of(10, 20)), parser.parse("12 April 2020 10:20", true));
        assertEquals(LocalDateTime.of(LocalDate.of(2020, 12, 15), LocalTime.of(4, 5)), parser.parse("4:05am 15 Dec 2020", true));
    }
}
