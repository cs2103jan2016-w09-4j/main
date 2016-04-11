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

        boolean timeFound = false,
                dateFound = false;

        LocalDate date = LocalDate.MIN;
        LocalTime time = defaultEndDay ? LocalTime.of(23, 59) : LocalTime.of(0, 0);

        for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
            for (int eIndex=timeString.length(); eIndex>sIndex; eIndex--) {
                for (int i = 0; i < dateFormats.length; i++) {
                    if (dateFound) break;

                    try {
                        // if the date format does not contain year info, get current year as default

                        DateTimeFormatter formatter = (dateFormats[i].contains("y")) ?
                            DateTimeFormatter.ofPattern(dateFormats[i]) :
                                new DateTimeFormatterBuilder()
                                .appendPattern(dateFormats[i])
                                .parseDefaulting(ChronoField.YEAR, LocalDateTime.now().getYear())
                                .toFormatter();

                        String tryString = timeString.substring(sIndex, eIndex);
                        date = LocalDate.parse(tryString, formatter);
                        dateFound = true;
                    }
                    catch (DateTimeParseException exc) {
                    }
                }
            }
        }

        for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
            for (int eIndex = timeString.length(); eIndex>sIndex; eIndex--) {
                for (int i = 0; i < timeFormats.length; i++) {
                    if (timeFound) break;

                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormats[i]);
                        String tryString = timeString.substring(sIndex, eIndex);
                        time = LocalTime.parse(tryString, formatter);
                        timeFound = true;
                    }
                    catch (DateTimeParseException exc) {

                    }
                }
            }
        }

        LocalDateTime nattyResult = null;

        try {
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(timeString);
            Date nattyDate = groups.get(0).getDates().get(0);
            nattyResult = LocalDateTime.ofInstant(nattyDate.toInstant(), ZoneId.systemDefault());
        }
        catch (Exception e) { // natty does not find any result
        }

        if (!dateFound && !timeFound && nattyResult == null) return null;

        if (!dateFound) date = nattyResult.toLocalDate();
        return LocalDateTime.of(date, time);
    }
}
