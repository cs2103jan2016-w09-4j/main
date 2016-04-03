//@@author Khanh
package parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class DescriptionParser {
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private LocalDateTime searchTime;
	private String description;
	private ArrayList<String> categories;

	public DescriptionParser(String input, String commandType) {
		input = input.trim();

		if (commandType.equals("add") || commandType.equals("edit")) {

			int startTimeIndex = input.indexOf("start");
			int endTimeIndex = input.indexOf("end");

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

			if (startTimeIndex == -1)
				startTimeIndex = input.length();
			if (endTimeIndex == -1)
				endTimeIndex = input.length();
			description = input.substring(0, Math.min(startTimeIndex, endTimeIndex)).trim();

		} else if (commandType.equals("search") || commandType.equals("searchdone")) {

			String firstCharacter = String.valueOf(input.charAt(0));
			categories = new ArrayList<String>();
			
			// search by category
			if (firstCharacter.equals("#")) {
				String[] categoryLine = input.split(" ");
				String categoryName;
				if (categoryLine.length == 1) { // only one category
					categoryName = getUserInput(input, "#");
					categories.add(categoryName);
				} else { // multiple categories
					for (int i = 0; i < categoryLine.length; i++) {
						categoryName = getUserInput(categoryLine[i], "#");
						categories.add(categoryName);
					}
				}
			} else {
				try {
					DateTimeParser dateTimeParser = new DateTimeParser();
					searchTime = dateTimeParser.parse(input, true);
				} catch (DateTimeParseException dtpe) { // description
					description = input;
				}
			}
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
	
	public LocalDateTime getSearchTime() {
		return searchTime;
	}

	public String getDescription() {
		return description;
	}
	
	public ArrayList<String> getCategories() {
		return categories;
	}
}
