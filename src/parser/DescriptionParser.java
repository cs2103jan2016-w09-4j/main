//@@author Khanh
package parser;

import java.time.LocalDateTime;

public class DescriptionParser {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    public DescriptionParser(String input) {
        input = input.trim();

        int startTimeIndex = input.indexOf("start");
        int endTimeIndex = input.indexOf("end");

        DateTimeParser dateTimeParser= new DateTimeParser();

        if (startTimeIndex!=-1) {
            int startTimeCutIndex = (endTimeIndex > startTimeIndex) ? endTimeIndex : input.length();
            String startTimeString = input.substring(startTimeIndex + 5, startTimeCutIndex);
            startTime = dateTimeParser.parse(startTimeString, false);
        }

        if (endTimeIndex!=-1) {
            int endTimeCutIndex = (startTimeIndex > endTimeIndex) ? startTimeIndex : input.length();
            String endTimeString = input.substring(endTimeIndex + 3, endTimeCutIndex);
            endTime = dateTimeParser.parse(endTimeString, true);
        }

        description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime(){
        return endTime;
    }

    public String getDescription(){
        return description;
    }
}
