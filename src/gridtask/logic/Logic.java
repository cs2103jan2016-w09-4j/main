//@@author A0123972A
package gridtask.logic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import gridtask.common.*;
import gridtask.common.Command.CommandType;
import gridtask.logic.Execution;
import gridtask.parser.EmptyCommandException;
import gridtask.parser.GeneralParser;
import gridtask.parser.InvalidCommandException;
import gridtask.parser.WrongCommandFormatException;


import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logic {

    // Objects to call into other classes
    private Execution execution;
    private GeneralParser parser;
    private static Logic logic = new Logic();
    private static Logger logger = Logger.getLogger("Logic");

    private static final int MAX_PREDICTIONS = 5;
    private static final Comparator<Entry<String, Integer>> freqComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    };

    public Logic() {
        this.execution = new Execution();
        this.parser = new GeneralParser();
        initializeLogger();
    }
    
    private void initializeLogger() {
        try {
            Handler fh = new FileHandler("log_logic_Logic");
            if (fh != null && logger != null) {
            	logger.addHandler(fh);
            	SimpleFormatter formatter = new SimpleFormatter();  
            	fh.setFormatter(formatter);
            }	
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Result processCommand(String input) {
        System.out.println(input);
        try {
            Command command = parser.parseCommand(input);
            logger.log(Level.INFO, "input successfully parsed");
            return execute(command);
        } catch (WrongCommandFormatException|EmptyCommandException|InvalidCommandException e) {
        	logger.log(Level.SEVERE, "failed to process command", e);
            return new Result();
        }
    }

    private Result execute(Command command){

        CommandType commandType = command.getType();

        LocalDateTime startDate = command.getStartDate();
        LocalDateTime endDate = command.getEndDate();

        // verify that start date is before end date
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
            	logger.log(Level.INFO, "invalid start date: Cannot have a later start date");
                return new Result(CommandType.INVALID, false, "Cannot have a later start date!", new ArrayList<Task>());
            }
        }

        execution.updateTaskProgress();

        switch(commandType) {

            case ADD :
            	return execution.addTask(command);

            case EDIT :
                return execution.editTask(command);

            case DELETE :
                return execution.deleteTask(command);

            case DONE :
                return execution.doneTask(command);

            case UNDO :
                return execution.undoCommand();

            case REDO :
                return execution.redoCommand();

            case SAVE :
                return execution.saveTasks(command);

            case LOAD :
                return execution.loadTasks(command);

            case SEARCH :
                return execution.searchTasks(command);

            case SEARCHDONE :
            	return execution.searchDoneTasks(command);

            case HOME :
                return execution.filterTasks();

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

    //@@author A0131507R
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
