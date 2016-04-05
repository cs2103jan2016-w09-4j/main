//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.util.ArrayList;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;

public class DescriptionParser {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String description;
	private ArrayList<String> categories;

	public DescriptionParser(String input) {
//	    Pattern pattern = Pattern.compile("#+\\w*\\b");

		input = input.trim();

		int startTimeIndex = input.indexOf("start");
		int endTimeIndex = input.indexOf("end");
		int categoryIndex = input.indexOf("#");

		DateTimeParser dateTimeParser = new DateTimeParser();

		if (startTimeIndex != -1) {
			int startTimeCutIndex = (endTimeIndex > startTimeIndex) ? endTimeIndex : input.length();
			String startTimeString = input.substring(startTimeIndex + 5, startTimeCutIndex);
			startTime = dateTimeParser.parse(startTimeString, false);
		}

		if (endTimeIndex != -1) {
			int endTimeCutIndex = (startTimeIndex > endTimeIndex) ? startTimeIndex : input.length();
			String endTimeString = input.substring(endTimeIndex + 3, endTimeCutIndex);
			endTime = dateTimeParser.parse(endTimeString, true);
		}

		if (startTimeIndex == -1) {
			startTimeIndex = input.length();
		}
		if (endTimeIndex == -1) {
			endTimeIndex = input.length();
		}
		description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();
		ArrayList<String> storeCategories = new ArrayList<String>();

		if (categoryIndex != -1) {
			String categoryString = input.substring(categoryIndex,input.length());
			String[] splitCategories = categoryString.split(" ");
			String categoryName;
			if (splitCategories.length == 1) {
				categoryName = getUserInput(categoryString, "#");
				storeCategories.add(categoryName);

			} else {
				for (int i = 0; i < splitCategories.length; i++) {
					categoryName = getUserInput(splitCategories[i], "#");
					storeCategories.add(categoryName);
				}
			}

			setCategories(storeCategories);

		}
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
