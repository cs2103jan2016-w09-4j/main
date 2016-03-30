package parser;

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
        timeString = timeString.trim();
        timeString = timeString.replace("am", "AM").replace("pm", "PM");

        boolean timeFound = false,
                dateFound = false;

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        for (int i = 0; i < dateFormats.length; i++) {
            for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
                for (int eIndex=timeString.length(); eIndex>sIndex; eIndex--) {
                    if (dateFound) break;

                    try {
                        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                .appendPattern(dateFormats[i])
                                .parseDefaulting(ChronoField.YEAR, LocalDateTime.now().getYear())
                                .toFormatter();

                        formatter = DateTimeFormatter.ofPattern(dateFormats[i]);

                        String tryString = timeString.substring(sIndex, eIndex);
                        System.out.println(tryString + " " + dateFormats[i]);
                        date = LocalDate.parse(tryString, formatter);
                        dateFound = true;
                    }
                    catch (DateTimeParseException exc) {
                    }
                }
            }
        }

        for (int i = 0; i < timeFormats.length; i++) {
            for (int sIndex = 0; sIndex<timeString.length(); sIndex++) {
                for (int eIndex = timeString.length(); eIndex>sIndex; eIndex--) {
                    if (timeFound) break;

                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormats[i]);
                        time = LocalTime.parse(timeString.substring(sIndex, eIndex), formatter);
                        timeFound = true;
                    }
                    catch (DateTimeParseException exc) {

                    }
                }
            }
        }


        if (dateFound) {
            if (timeFound) {
                return LocalDateTime.of(date, time);
            }
            else {
                if (defaultEndDay) {
                    return LocalDateTime.of(date, LocalTime.of(23, 59));
                }
                else {
                    return LocalDateTime.of(date, LocalTime.of(0, 0));
                }
            }
        } else if (timeFound) {
            return LocalDateTime.of(LocalDate.now(), time);
        }

        try {
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(timeString);
            Date nattyDate = groups.get(0).getDates().get(0);
            return LocalDateTime.ofInstant(nattyDate.toInstant(), ZoneId.systemDefault());
        }
        catch (Exception e) { // time string does not represent any valid time
            return null;
        }
    }
}
