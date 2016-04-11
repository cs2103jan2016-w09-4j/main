//@@author Khanh
package gridtask.parser;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class DateTimeParser {
    private final String[] dateFormats = {
            "dd-MM-yyyy",
            "dd-MMM-yyyy",
            "dd-MMMM-yyyy",
            "dd MMM yyyy",
            "dd MMMM yyyy",

            "d-M-yyyy",
            "d-MMM-yyyy",
            "d-MMMM-yyyy",
            "d MMM yyyy",
            "d MMMM yyyy",

            "yyyy-MM-dd",
            "yyyy-MMM-dd",
            "yyyy-MMMM-dd",
            "yyyy MMM dd",
            "yyyy MMMM dd",

            "yyyy-M-d",
            "yyyy-MMM-d",
            "yyyy-MMMM-d",
            "yyyy MMM d",
            "yyyy MMMM d",

            "dd/MM/yyyy",
            "d/M/yyyy",
            "yyyy/mm/dd",
            "yyyy/m/d",

            "dd-MM-yy",
            "dd-MMM-yy",
            "dd-MMMM-yy",
            "dd MMM yy",
            "dd MMMM yy",

            "d-M-yy",
            "d-MMM-yy",
            "d-MMMM-yy",
            "d MMM yy",
            "d MMMM yy",

            "dd/MM/yy",
            "d/M/yy",

            "d/M",
            "d-M",
            "dd/MM",
            "dd/MMM",
            "dd/MMMM",
            "dd MM",
            "dd MMM",
            "dd MMMM",
            "d MMM",
            "d MMMM"
    };

    private final String[] timeFormats = {
            "HH:mm",
            "H:m",
            "hh[:mm]a",
            "h[:m]a"
    };


    public DateTimeParser() {

    }

    public LocalDateTime parse(String timeString, boolean defaultEndDay) {
        timeString = timeString.trim().replace("am", "AM").replace("pm", "PM");

        LocalDate date = LocalDate.MIN;
        LocalTime time = defaultEndDay ? LocalTime.of(23, 59) : LocalTime.of(0, 0);

        boolean timeFound = false,
                dateFound = false;

        for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
            for (int eIndex=timeString.length(); eIndex>sIndex; eIndex--) {
                for (int i = 0; i < dateFormats.length; i++) {
                    if (dateFound) break;
                    try {
                        date = tryToParseDate(timeString, sIndex, eIndex, i);
                        dateFound = true;
                    }
                    catch (DateTimeParseException exc) {
                        // the String does not match the pattern tried
                    }
                }
            }
        }

        for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
            for (int eIndex = timeString.length(); eIndex>sIndex; eIndex--) {
                for (int i = 0; i < timeFormats.length; i++) {
                    if (timeFound) break;

                    try {
                        time = tryToParseTime(timeString, sIndex, eIndex, i);
                        timeFound = true;
                    }
                    catch (DateTimeParseException exc) {
                        // the String does not match the pattern tried
                    }
                }
            }
        }

        LocalDateTime nattyResult = getNattyResult(timeString);
        return getCombinedResult(timeFound, dateFound, date, time, nattyResult);
    }

    /**
     * use Natty Library to get the date time to combine with self-calculated result
     */

    public LocalDateTime getNattyResult(String timeString) {
        LocalDateTime nattyResult = null;

        try {
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(timeString);
            Date nattyDate = groups.get(0).getDates().get(0);
            nattyResult = LocalDateTime.ofInstant(nattyDate.toInstant(), ZoneId.systemDefault());
        }
        catch (Exception e) { // natty does not find any result
        }
        return nattyResult;
    }

    /**
     * Combine result from natty
     * and self-calculated result
     */
    public LocalDateTime getCombinedResult(boolean timeFound, boolean dateFound, LocalDate date, LocalTime time,
            LocalDateTime nattyResult) {
        if (!dateFound && !timeFound && nattyResult == null) return null;
        if (!dateFound) date = nattyResult.toLocalDate();
        return LocalDateTime.of(date, time);
    }

    public LocalDate tryToParseDate(String timeString, int sIndex, int eIndex, int patternIndex) {
        LocalDate date;
        // if the date format does not contain year info, get current year as default
        DateTimeFormatter formatter = (dateFormats[patternIndex].contains("y")) ?
            DateTimeFormatter.ofPattern(dateFormats[patternIndex]) :
                new DateTimeFormatterBuilder()
                .appendPattern(dateFormats[patternIndex])
                .parseDefaulting(ChronoField.YEAR, LocalDateTime.now().getYear())
                .toFormatter();

        String tryString = timeString.substring(sIndex, eIndex);
        date = LocalDate.parse(tryString, formatter);
        return date;
    }

    public LocalTime tryToParseTime(String timeString, int sIndex, int eIndex, int patternIndex) {
        LocalTime time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormats[patternIndex]);
        String tryString = timeString.substring(sIndex, eIndex);
        time = LocalTime.parse(tryString, formatter);
        return time;
    }
}
