//@@author A0098084U
package gridtask.parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskDetails {
    
    private static final String START_TIME_MARKER = "\\bstart\\b";
    private static final String END_TIME_MARKER = "\\bend\\b";
    private static final String CATEGORY_MARKER = "#";
    private static final String WRONG_POSITION_OF_CATEGORY_NOTIFY = "# should be put after time";

    private String description;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private ArrayList<String> categories;

	public TaskDetails(String description, LocalDateTime startTime, LocalDateTime endTime, ArrayList<String> categories) {
	    this.description = description;
	    this.startTime = startTime;
	    this.endTime = endTime;
	    this.categories = categories;
	}

	public TaskDetails(String input) throws WrongCommandFormatException {
		Pattern startTimePattern = Pattern.compile(START_TIME_MARKER);
        Matcher startTimeMatcher = startTimePattern.matcher(input);
        int startTimeIndex = startTimeMatcher.find() ? startTimeMatcher.start() : -1;

		Pattern endTimePattern = Pattern.compile(END_TIME_MARKER);
		Matcher endTimeMatcher = endTimePattern.matcher(input);
		int endTimeIndex = endTimeMatcher.find() ? endTimeMatcher.start() : -1;

		input = extractCategories(input, startTimeIndex, endTimeIndex);

        extractTime(input, startTimeMatcher, startTimeIndex, endTimeMatcher, endTimeIndex);

		extractDescription(input, startTimeIndex, endTimeIndex);
	}

    public void extractTime(String input, Matcher startTimeMatcher, int startTimeIndex, Matcher endTimeMatcher,
            int endTimeIndex) {
        DateTimeParser dateTimeParser = new DateTimeParser();

        if (startTimeIndex != -1) {
			int startTimeCutIndex = (endTimeIndex > startTimeIndex) ? endTimeIndex : input.length();
			String startTimeString = input.substring(startTimeMatcher.end(), startTimeCutIndex);
			startTime = dateTimeParser.parse(startTimeString, false);
		}

		if (endTimeIndex != -1) {
			int endTimeCutIndex = (startTimeIndex > endTimeIndex) ? startTimeIndex : input.length();
			String endTimeString = input.substring(endTimeMatcher.end(), endTimeCutIndex);
			endTime = dateTimeParser.parse(endTimeString, true);
		}
    }

    public void extractDescription(String input, int startTimeIndex, int endTimeIndex) {
        if (startTimeIndex == -1) {
			startTimeIndex = input.length();
		}
		if (endTimeIndex == -1) {
			endTimeIndex = input.length();
		}
		description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();
    }

    /**
     * Find the categories in the description
     * Return the description after categories are cut out
     */

    public String extractCategories(String input, int startTimeIndex, int endTimeIndex)
            throws WrongCommandFormatException {
        int categoryIndex = input.indexOf(CATEGORY_MARKER);

		if (categoryIndex>-1 && categoryIndex < Integer.max(startTimeIndex, endTimeIndex)) {
		    throw new WrongCommandFormatException(WRONG_POSITION_OF_CATEGORY_NOTIFY);
		}

		categories = new ArrayList<String>();

        if (categoryIndex != -1) {
            String categoryString = input.substring(categoryIndex, input.length());
            String[] splitCategories = categoryString.split("\\s+");

            for (int i = 0; i < splitCategories.length; i++) {
                String categoryName = splitCategories[i].replace(CATEGORY_MARKER, "");
                categories.add(categoryName.toLowerCase());
            }
        }

        if (categoryIndex!=-1) {
            input = input.substring(0, categoryIndex).trim();
        }
        return input;
    }

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<String> getCategories() {
		return categories;
	}
	
}
