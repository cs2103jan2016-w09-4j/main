//@@author Gilbert
package logic;

import common.*;
import common.Command.CommandType;
import parser.GeneralParser;
import parser.WrongCommandFormatException;
import parser.EmptyCommandException;
import parser.InvalidCommandException;
import storage.Storage;
import logic.Execution;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.time.LocalDateTime;

public class Logic {

    // Objects to call into other classes
    private Execution execution;
    private Storage storage;
    private GeneralParser parser;
    private static Logic logic = new Logic();

    private ArrayList<Task> list;

	private static final String CATEGORY_PRIORITY = "priority";
    private static final int MAX_PREDICTIONS = 5;
    private static final Comparator<Entry<String, Integer>> freqComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    };

    public Logic() {
        this.execution = new Execution();
        this.storage = new Storage();
        this.parser = new GeneralParser();
        this.list = storage.getMainList();
    }

    private Result execute(Command command){

        CommandType commandType = command.getType();
        String description = command.getDescription();
        int taskID = command.getId();

        LocalDateTime startDate = command.getStartDate();
        LocalDateTime endDate = command.getEndDate();
        ArrayList<String> categories = command.getCategories();

        // verify that start date is before end date
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                return new Result(CommandType.INVALID, false, "Cannot have a later start date!", new ArrayList<Task>());
            }
        }

        execution.updateTaskProgress();

        switch(commandType) {

            case ADD :
            	return execution.addTask(command);

            case DELETE :
                return execution.deleteTask(command);

            case EDIT :
                return execution.editTask(command);

            case SEARCH :
            	return execution.searchTasks(command);

            case HOME :
                /*
                execution.sortList(execution.getMainList());
                list = execution.getWeekList();
                execution.updateTaskProgress();
                */
                return execution.filterTasks();

            case SAVE :
                return execution.saveTasks(command);

            case LOAD :
                return execution.loadTasks(command);

            case UNDO :
                return execution.undoCommand();

            case REDO :
                return execution.redoCommand();

            case DONE :
                return execution.doneTask(command);

            case SEARCHDONE :
            	if (categories != null) {
            		ArrayList<Task> searchDoneResult = new ArrayList<Task>();
            		list = execution.getDoneList();
            		if (categories.get(0).equals(CATEGORY_PRIORITY)) {
            			for (Task task : list) {
            				if (task.isImportant()) {
            					searchDoneResult.add(task);
            				}
            			}
            		} else {
            			for (Task task : list) {
            				boolean put = true;
            				for (String category : categories) {
            					category = execution.toSentenceCase(category);
            					if (!task.getCategories().contains(category)) {
            						put = false;
            						break;
            					}
            				}
            				if (put) {
        						searchDoneResult.add(task);
            				}
        				}
            		}

        		return new Result(commandType, true, "Showing completed tasks", searchDoneResult);

        		} else if (command.getDescription().length() == 0) {
            		list = execution.getDoneList();
            		return new Result(commandType, true, "Showing completed tasks", list);
            	} else {
            		ArrayList<Task> searchDoneResult = new ArrayList<Task>();
            		list = execution.getDoneList();
            		description = description.toLowerCase();
            		for (Task task : list) {
            			String descriptionLowerCase = task.getDescription().toLowerCase();
            			if (descriptionLowerCase.contains(description)) {
            				searchDoneResult.add(task);
            			}
            		}
            		return new Result(commandType, true, "Showing completed tasks", searchDoneResult);
            	}

            case HELP :
                return new Result(commandType, true, "Help", null);

            case EXIT :
                System.exit(0);

            case INVALID :
                return new Result();

            default :
                return new Result();

        }
    }

    public Result processCommand(String input) {
        System.out.println(input);
        try {
            Command command = parser.parseCommand(input);
            return execute(command);
        } catch (WrongCommandFormatException|EmptyCommandException|InvalidCommandException e) {
            return new Result();
        }
    }

    public static Logic getInstance() {
        if (logic == null){
            return logic = new Logic();
        }
        return logic;
    }

    public Execution getExecutionInstance() {
    	if (execution == null){
    		return execution = new Execution();
    	}
    	return execution;
    }

    public ArrayList<Task> getMainList() {
        return execution.getMainList();
    }

    public ArrayList<Task> getDoneList() {
        return execution.getDoneList();
    }

    public ArrayList<Category> getCategories() {
        TreeSet<Entry<String, Integer>> list = execution.getCategories();
        ArrayList<Category> categories = new ArrayList<Category>();
        Iterator<Entry<String, Integer>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Entry<String, Integer> next = iterator.next();
            categories.add(new Category(next.getKey(), next.getValue()));
        }
        return categories;
    }

    public ArrayList<String> getPredictions(String input) {
        String inputTrimmed = input.trim();
        if (inputTrimmed.isEmpty()) {
            return null;
        }
        String[] params = inputTrimmed.split("\\s+", 2);
        String firstWord = params[0];
        if (firstWord.equalsIgnoreCase("add")) {
            return getPredictionsForAdd(params);
        } else if (firstWord.equalsIgnoreCase("search")) {
            //if (input.matches("\\s*search\\s+.*")) {
                return getPredictionsForSearch(params);
            //}
        } else if (firstWord.equalsIgnoreCase("edit")) {
            return getPredictionsForEdit(params);
        } else if (firstWord.equalsIgnoreCase("save") || firstWord.equalsIgnoreCase("load")) {
            return getPredictionsForSaveLoad(params);
        }

        return null;
    }

    //@@author Ruoling
    private ArrayList<String> getPredictionsForAdd(String[] params) {
        // Predictions based on task descriptions that user previously entered
        TreeSet<Entry<String, Integer>> dictionary = execution.getTaskDictionary();
        return getPredictionsFromDictionary(dictionary, params);
    }

    private ArrayList<String> getPredictionsForSearch(String[] params) {
        // Predictions based on task descriptions that user previously entered
        TreeSet<Entry<String, Integer>> dictionary = execution.getWordDictionary();
        return getPredictionsFromDictionary(dictionary, params);
    }

    private ArrayList<String> getPredictionsForEdit(String[] params) {
        assert (params.length > 0);

        // Maintain a unique set of prediction strings
        HashSet<String> hashSet = new HashSet<String>();
        // Predictions based on task descriptions that the user previously entered
        TreeSet<Entry<String, Integer>> dictionary = execution.getTaskDictionary();
        // List of task descriptions sorted by frequency
        ArrayList<Entry<String, Integer>> freqList;

        assert (dictionary != null);

        String command = params[0];
        if (params.length == 2) {
            try {
                // retrieve task description
                int id = Integer.parseInt(params[1]);
                String desc = execution.getMainList().get(id-1).getDescription();
                hashSet.add(command + " " + id + " " + desc);

                // retrieve all entries, sorted by frequency
                freqList = sortByFrequency(dictionary);

                // get maximum number of predictions
                for (Entry<String, Integer> entry : freqList) {
                    String prediction = entry.getKey();
                    hashSet.add(command + " " + id + " " + prediction);
                    if (hashSet.size() == MAX_PREDICTIONS) {
                        break;
                    }
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // wrong task index, no predictions given
            }
        }

        ArrayList<String> predictions = new ArrayList<String>();
        predictions.addAll(hashSet);
        return predictions;
    }

    private ArrayList<String> getPredictionsForSaveLoad(String[] params) {
        // Predictions based on directories/file names the user previously entered
        TreeSet<Entry<String, Integer>> dictionary = execution.getFileDictionary();
        return getPredictionsFromDictionary(dictionary, params);
    }

    private ArrayList<String> getPredictionsFromDictionary(
            TreeSet<Entry<String, Integer>> dictionary, String[] params) {
        assert (dictionary != null);
        assert (params.length > 0);

        // Maintain a unique set of prediction strings
        HashSet<String> hashSet = new HashSet<String>();
        // List of task descriptions sorted by frequency
        ArrayList<Entry<String, Integer>> freqList;

        String command = params[0];
        if (params.length == 1) {
            // retrieve all entries, sorted by frequency
            freqList = sortByFrequency(dictionary);
        } else {
            String minArg = params[1];
            int lastIndex = minArg.length() - 1;
            char c = minArg.charAt(lastIndex);
            c++;
            String maxArg = minArg.substring(0, lastIndex) + c;
            Entry<String, Integer> min = new AbstractMap.SimpleEntry<String, Integer>(minArg, 1);
            Entry<String, Integer> max = new AbstractMap.SimpleEntry<String, Integer>(maxArg, 1);

            // retrieve subset of entries matching user input, sorted by frequency
            SortedSet<Entry<String,Integer>> matches = dictionary.subSet(min, true, max, false);
            freqList = sortByFrequency(matches);
        }

        // get maximum number of predictions
        for (Entry<String, Integer> entry : freqList) {
            String prediction = entry.getKey();
            hashSet.add(command + " " + prediction + " ");
            if (hashSet.size() == MAX_PREDICTIONS) {
                break;
            }
        }

        ArrayList<String> predictions = new ArrayList<String>();
        predictions.addAll(hashSet);
        return predictions;
    }

    private ArrayList<Entry<String, Integer>> sortByFrequency(Set<Entry<String, Integer>> dictionary) {
        ArrayList<Entry<String, Integer>> freq = new ArrayList<Entry<String, Integer>>();
        freq.addAll(dictionary);
        freq.sort(freqComparator.reversed());
        return freq;
    }

}
