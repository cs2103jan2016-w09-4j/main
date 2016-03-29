//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import com.joestelmach.natty.*;

public class DescriptionParser {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    public DescriptionParser(String input) {
        input = input.trim();

        int startTimeIndex = input.indexOf("start");
        int endTimeIndex = input.indexOf("end");

        if (startTimeIndex!=-1) {
            int startTimeCutIndex = (endTimeIndex > startTimeIndex) ? endTimeIndex : input.length();
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(input.substring(startTimeIndex + 5, startTimeCutIndex));
            Date date = groups.get(0).getDates().get(0);
            startTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }

        if (endTimeIndex!=-1){
            int endTimeCutIndex = (startTimeIndex > endTimeIndex) ? startTimeIndex : input.length();
            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(input.substring(endTimeIndex + 3, endTimeCutIndex));
            Date date = groups.get(0).getDates().get(0);
            endTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }

        description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();
    }

    public LocalDateTime getStartTime(){
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public String getDescription(){
        return description;
    }
}
