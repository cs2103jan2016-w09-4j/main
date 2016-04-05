//@@author Gilbert
package logic;

import common.*;
import common.Command.CommandType;
import parser.GeneralParser;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Logic {

    // Objects to call into other classes
    private GeneralParser parser;
    private Storage storage;
    private Execution execution;
    private static Logic logic = new Logic();
    
    private ArrayList<Task> list;
    private ArrayList<Task> weekList;
    
    private static final int MAX_PREDICTIONS = 5;
    private static final Comparator<Entry<String, Integer>> freqComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    };
    
    public Logic() {
        this.parser = new GeneralParser();
        this.storage = new Storage();
        this.execution = new Execution();
        this.list = storage.getMainList();
        weekList = new ArrayList<Task>();
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
        
        sortWeekList();
        execution.updateTaskProgress();
        
        switch(commandType) {
        
            case ADD :
                return execution.addTask(description, startDate, endDate, categories);
            
            case DELETE :
                return execution.deleteTask(taskID);
            
            case EDIT :
                return execution.editTask(taskID, description, startDate, endDate, categories);
                
            case SEARCH :
            	if (categories != null) {
            		return execution.searchTask(categories);
            	}
            	else {
            		return execution.searchTask(description);
            	}
            	
            case HOME :
                list = storage.getMainList();
                execution.setMainList(list);
                execution.updateTaskProgress();
                return new Result(commandType, true, "Return home", list);
                
            case SAVE :
                return execution.savingTasks(description);
                
            case LOAD :
                return execution.loadingTasks(description);

            case UNDO :
                return execution.undoCommand();
                
            case REDO : 
                return execution.redoCommand();
                
            case DONE :
                return execution.completeCommand(taskID);
                
            case SEARCHDONE :
                list = execution.getDoneList();
                return new Result(commandType, true, "Showing completed tasks", list);

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
        Command command = parser.parseCommand(input);
        return execute(command);
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
    
    public ArrayList<Task> getWeekList() {
    	return weekList;
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
        input = input.trim();
        if (input.isEmpty()) {
            return null;
        }
        String[] params = input.split("\\s+", 2);
        String firstWord = params[0];
        if (firstWord.equalsIgnoreCase("add")) {
            return getPredictionsForAdd(params);
        } else if (firstWord.equalsIgnoreCase("search")) {
            return getPredictionsForSearch(params);
        } else if (firstWord.equalsIgnoreCase("edit")) {
            return getPredictionsForEdit(params);
        } else if (firstWord.equalsIgnoreCase("save") || firstWord.equalsIgnoreCase("load")) {
            return getPredictionsForSaveLoad(params);
        }
        
        return null;
    }
    
    private void sortWeekList(){
    	
    	weekList.clear();
    	ArrayList<Task> list = execution.getMainList();
    	
    	LocalDate today = LocalDate.now();
    	LocalDate weekToday = today.plusWeeks(1);
    	
		for (Task task : list){
			LocalDateTime taskEnd = task.getEndDate();
			if(taskEnd != null){
				
				LocalDate taskEndDate = taskEnd.toLocalDate();
				if(taskEndDate != null){
					if (today.compareTo(taskEndDate) <= 0 && taskEndDate.compareTo(weekToday) < 0){
						weekList.add(task);
					}		
				}
			}
		}
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
            hashSet.add(command + " " + prediction);
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
