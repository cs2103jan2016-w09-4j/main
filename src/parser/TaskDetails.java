//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import parser.DateTimeParser;

public class TaskDetails {
    private static final String START_TIME_MARKER = "\\bstart\\b";
    private static final String END_TIME_MARKER = "\\bend\\b";
    private static final String WRONG_POSITION_OF_CATEGORY_NOTIFY = "# should be put after time";

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String description;
	private ArrayList<String> categories;

	public TaskDetails(String input) throws WrongCommandFormatException {
		input = input.trim();

		Pattern startTimePattern = Pattern.compile(START_TIME_MARKER);
		Pattern endTimePattern = Pattern.compile(END_TIME_MARKER);

		Matcher startTimeMatcher = startTimePattern.matcher(input);
		Matcher endTimeMatcher = endTimePattern.matcher(input);

		int startTimeIndex = startTimeMatcher.find() ? startTimeMatcher.start() : -1; //input.indexOf(" start ");
		int endTimeIndex = endTimeMatcher.find() ? endTimeMatcher.start() : -1; //input.indexOf(" end ");
		int categoryIndex = input.indexOf("#");

		if (categoryIndex < Integer.max(startTimeIndex, endTimeIndex)) {
		    throw new WrongCommandFormatException(WRONG_POSITION_OF_CATEGORY_NOTIFY);
		}

		DateTimeParser dateTimeParser = new DateTimeParser();

        ArrayList<String> storeCategories = new ArrayList<String>();

        if (categoryIndex != -1) {
            String categoryString = input.substring(categoryIndex, input.length());
            String[] splitCategories = categoryString.split(" ");
            String categoryName;
            if (splitCategories.length == 1) {
                categoryName = getUserInput(categoryString, "#");
                storeCategories.add(categoryName);

            } else {
                for (int i = 0; i < splitCategories.length; i++) {
                    categoryName = getUserInput(splitCategories[i], "#");
                    storeCategories.add(categoryName.toLowerCase());
                }
            }

            setCategories(storeCategories);
        }

        if (categoryIndex!=-1) input = input.substring(0, categoryIndex).trim();

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

		if (startTimeIndex == -1) {
			startTimeIndex = input.length();
		}
		if (endTimeIndex == -1) {
			endTimeIndex = input.length();
		}
		description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();
	}

	private static String getUserInput(String line, String toReplace) {
		String userInput = line.replace(toReplace, "").trim();

		return userInput;
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

	public void setCategories(ArrayList<String> list) {
		categories = list;
	}
}
