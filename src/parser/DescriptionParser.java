package parser;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;

public class DescriptionParser {
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;

    public DescriptionParser(String input) {
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("H:m d-M-yy"),
                DateTimeFormatter.ofPattern("H:m d-M-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        };

        input = input.trim();

        try {
            int startIndex = input.indexOf(" start");
            if (startIndex!=-1) {
                int cutIndex = input.length();
                for (int i = startIndex + 7; i<input.length(); i++) {
                    if (Character.isLetter(input.charAt(i))){
                        cutIndex = i-1;
                        break;
                    }
                }

                boolean parsed = false;
                for (DateTimeFormatter formatter: formatters){
                    if (parsed) break;
                    try {
                        String timeString = input.substring(startIndex+7, cutIndex);
                        start =  LocalDateTime.parse(timeString, formatter);
                    }
                    catch (DateTimeParseException exc){
                        parsed = !parsed;
                    }
                    finally {
                        parsed = !parsed;
                    }
                }
            }
            else {
                startIndex = input.length();
            }

            int endIndex = input.indexOf(" end");
            if (endIndex!=-1){
                int cutIndex = input.length();
                for (int i = endIndex + 5; i<input.length(); i++) {
                    if (Character.isLetter(input.charAt(i))){
                        cutIndex = i-1;
                        break;
                    }
                }

                boolean parsed = false;
                for (DateTimeFormatter formatter: formatters){
                    if (parsed) break;
                    try {
                        end =  LocalDateTime.parse(input.substring(endIndex+5, cutIndex), formatter);
                    }
                    catch (DateTimeParseException exc){
                        parsed = !parsed;
                    }
                    finally {
                        parsed = !parsed;
                    }
                }
            }
            else {
                endIndex = input.length();
            }

            description = input.substring(0, Math.min(startIndex, endIndex)).trim();
        }
        catch(DateTimeParseException exc){
            description = input;
        }
    }

    public LocalDateTime getStartTime(){
        return start;
    }

    public LocalDateTime getEndTime(){
        return end;
    }

    public String getDescription(){
        return description;
    }
}
