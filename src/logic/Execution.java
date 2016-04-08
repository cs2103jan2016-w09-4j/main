package logic;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import common.Command;
import common.Result;
import common.Task;
import common.Command.CommandType;
import storage.Storage;

public class Execution {
    
    private static Storage storage;
    
    private static ArrayList<Task> mainList;
    private static ArrayList<Task> weekList;
    private static ArrayList<Task> doneList;
    
    private static ArrayList<Task> previousCopyOfMainList;
    private static ArrayList<Task> previousCopyOfDoneList;
    private static ArrayList<Task> copyOfMainListForRedo;
    private static ArrayList<Task> copyOfDoneListForRedo;

    // Store user input history for auto-completion
    // TreeSet is used to avoid duplicate entries and for faster lookup
    // Integer is the frequency that the String is entered by the user
    private static TreeSet<Entry<String, Integer>> taskDictionary;
    private static TreeSet<Entry<String, Integer>> wordDictionary;
    private static TreeSet<Entry<String, Integer>> fileDictionary;

    // Store information about categories
    // Integer is the number of tasks belonging to a category
    private static TreeSet<Entry<String, Integer>> categories;
    
    // Keep track if user is allowed to perform undo or redo commands
    private static boolean canUndo;
    private static boolean canRedo;
    
    private static final String CATEGORY_PRIORITY = "Priority";
    private static final String CATEGORY_TODAY = "Today";

    // Common English function words, will not be stored in word dictionary
    private static final String[] functionWords = { "a", "about", "an", "and", "as", "at",
                                                    "by", "for", "in", "of","or", 
                                                    "the", "to", "with" };

    // Comparator where element uniqueness depends only on
    // the String key and not the frequency count
    private static final Comparator<Entry<String, Integer>> keyComparator = new Comparator<Entry<String, Integer>>() {
        public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
            // element uniqueness depends only on the entry's key
            return entry1.getKey().compareToIgnoreCase(entry2.getKey());
        }
    };

    public Execution() {
        storage = new Storage();
        // load tasks from storage
        mainList = storage.getMainList();
        doneList = storage.getCompletedList();
        weekList = new ArrayList<Task>();
        // sort tasks
        sortList(mainList);
        updateWeekList();
        initializeTaskProgress();

        // for undo and redo
        previousCopyOfMainList = new ArrayList<Task>();
        previousCopyOfDoneList = new ArrayList<Task>();
        copyOfMainListForRedo = new ArrayList<Task>();
        copyOfDoneListForRedo = new ArrayList<Task>();
        canUndo = false;
        canRedo = false;

        initializeDictionary();
        initializeCategories();
    }

    private void initializeTaskProgress() {
        categories = new TreeSet<Entry<String, Integer>>(keyComparator);
        updateTaskProgress();
    }
    private void initializeDictionary() {
        taskDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
        wordDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
        fileDictionary = new TreeSet<Entry<String, Integer>>(keyComparator);
        ArrayList<String> autocompletionList = storage.getAutoCompletionList();
        for (String entry : autocompletionList) {
            entry = toSentenceCase(entry);
            if (!entry.isEmpty()) {
                updateTaskDictionary(entry);
                updateWordDictionary(entry);
            }
        }
    }
    
    private void initializeCategories() {
        categories = new TreeSet<Entry<String, Integer>>(keyComparator);
        updateTaskProgress();
    }
    
    /*********************************
     * METHODS FOR COMMAND EXECUTION *
     *********************************/
    
    // CRUD commands
    
    public Result addTask(Command command) {
        assert (command != null);
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        String description = command.getDescription();
        LocalDateTime start = command.getStartDate();
        LocalDateTime end = command.getEndDate();
        ArrayList<String> inputCategories = command.getCategories();
        
        // validate user input
        if (description == null || description.isEmpty()) {
            return new Result(CommandType.ADD, false, "No description!", weekList);
        }
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                return new Result(CommandType.ADD, false, "Cannot have a later start date!", weekList);                
            }
        }
        
        // create a new Task with specified details
        Task newTask = new Task(description);
        newTask.setModified(true);
        setStartEnd(start, end, newTask);
        setCategories(inputCategories, newTask);
        
        // add to list
        mainList.add(newTask);
        
        // postprocessing
        sortList(mainList);
        updateWeekList();
        updateDictionary(description);
        updateTaskProgress();
        canUndo = true;
        canRedo = false;
        
        // save
        try {
            saveMainList(mainList);
        } catch (IOException ioe) {
            return new Result(CommandType.ADD, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.ADD, true, "Added task", weekList);
    }

    public Result editTask(Command command) {
        assert (command != null);
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        int taskId = command.getId();
        String description = command.getDescription();
        LocalDateTime start = command.getStartDate();
        LocalDateTime end = command.getEndDate();
        ArrayList<String> inputCategories = command.getCategories();
        
        // validate user input
        int index = taskId - 1;
        if (!isValidIndex(index)) {
            return new Result(CommandType.EDIT, false, "Wrong task number", weekList);
        }
        if (description == null || description.isEmpty()) {
            return new Result(CommandType.EDIT, false, "No description!", weekList);
        }
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                return new Result(CommandType.EDIT, false, "Cannot have a later start date!", weekList);                
            }
        }

        
        // create a new Task with specified details
        Task newTask = new Task(description);
        newTask.setModified(true);
        setStartEnd(start, end, newTask);
        setCategories(inputCategories, newTask);
        
        // remove specified task from list and add new task
        mainList.remove(index);
        mainList.add(newTask);

        // postprocessing
        sortList(mainList);
        updateWeekList();
        updateDictionary(description);
        updateTaskProgress();
        canUndo = true;
        canRedo = false;
        
        // save
        try {
            saveMainList(mainList);
        } catch (IOException ioe) {
            return new Result(CommandType.EDIT, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.EDIT, true, "Edited task", weekList);
    }
    
    public Result deleteTask(Command command) {
        assert (command != null);
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        
        int taskId = command.getId();
        
        // validate user input
        int index = taskId - 1;
        if (!isValidIndex(index)) {
            return new Result(CommandType.DELETE, false, "Wrong task number", weekList);
        }

        // remove from list
        mainList.remove(index);
        
        // postprocessing
        sortList(mainList);
        updateWeekList();
        updateTaskProgress();
        canUndo = true;
        canRedo = false;
        
        // save
        try {
            saveMainList(mainList);
        } catch (IOException ioe) {
            return new Result(CommandType.DELETE, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.DELETE, true, "Deleted task", weekList);
    }
    
    public Result doneTask(Command command) {
        assert (command != null);
        // preprocessing
        clearModifiedStatus();
        saveMainListForUndo();
        saveDoneListForUndo();
        
        int taskId = command.getId();
        
        // validate user input
        int index = taskId - 1;
        if (!isValidIndex(index)) {
            return new Result(CommandType.DONE, false, "Wrong task number", weekList);
        }

        // remove from main list and add to list of done tasks
        Task doneTask = mainList.remove(index);
        doneList.add(doneTask);
        
        // postprocessing
        sortList(mainList);
        updateWeekList();
        updateTaskProgress();
        canUndo = true;
        canRedo = false;
        
        // save
        try {
            saveMainAndDoneList(mainList, doneList);
        } catch (IOException ioe) {
            return new Result(CommandType.DONE, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.DONE, true, "Marked as done", weekList);
    }

    // Undo and redo commands
    
    public Result undoCommand() {
        if (!canUndo) {
            canRedo = false;
            return new Result(CommandType.UNDO, false, "Cannot undo previous command", mainList);
        }

        // transfer content from previousCopyOfMainList to mainList
        copyOfMainListForRedo.clear();
        copyOfMainListForRedo.addAll(mainList);
        mainList.clear();
        mainList.addAll(previousCopyOfMainList);

        // transfer content from previousCopyOfDoneList to doneList
        copyOfDoneListForRedo.clear();
        copyOfDoneListForRedo.addAll(doneList);
        doneList.clear();
        doneList.addAll(previousCopyOfDoneList);
        
        // post-processing
        sortList(mainList);
        updateWeekList();
        updateTaskProgress();
        canUndo = false;
        canRedo = true;
        
        // save 
        try {
            saveMainAndDoneList(mainList, doneList);
        } catch (IOException ioe) {
            return new Result(CommandType.UNDO, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.UNDO, true, "Last command undone", weekList);
    }
    
    public Result redoCommand() {
        if (!canRedo) {
            return new Result(CommandType.REDO, false, "Previous command was not undo", weekList);
        }

        // transfer content from copyOfMainListForRedo to mainList
        mainList.clear();
        mainList.addAll(copyOfMainListForRedo);

        // transfer content from copyOfDoneListForRedo to doneList
        doneList.clear();
        doneList.addAll(copyOfDoneListForRedo);
        
        // post-processing
        sortList(mainList);
        updateWeekList();
        canUndo = false;
        canRedo = false;
        
        // save 
        try {
            saveMainAndDoneList(mainList, doneList);
        } catch (IOException ioe) {
            return new Result(CommandType.REDO, false, "Couldn't save", weekList);
        }
        
        return new Result(CommandType.UNDO, true, "Redone", weekList);
    }
  
    // File related commands
    
    public Result saveTasks(Command command) {
        assert (command != null);
        
        String saveLocation = command.getDescription().trim();
        
        // save
        try {
            if (saveLocation.contains(" ")) {
                String[] params = saveLocation.split(" ");
                String directory = params[0].toLowerCase();
                String fileName = params[1];
                storage.saveToFileWithDirectory(directory, fileName);
            } else {
                storage.saveToFile(saveLocation);
            }

            /// post-processing
            updateFileDictionary(saveLocation);
            canUndo = false;
            canRedo = false;
            
            return new Result(CommandType.SAVE, true, "Saved at " + saveLocation, weekList);
        } catch (IOException ioe) {
            return new Result(CommandType.SAVE, false, "Couldn't save data at " + saveLocation, mainList);
        }
    }
    
    public Result loadTasks(Command command) {
        assert (command != null);
        // preprocessing
        clearModifiedStatus();
        
        // store temp copy of original list in case loading of file fails
        ArrayList<Task> tempMainList = new ArrayList<Task>();
        tempMainList.addAll(mainList);
        ArrayList<Task> tempDoneList = new ArrayList<Task>();
        tempDoneList.addAll(doneList);
        
        String loadLocation = command.getDescription().trim();
        
        // load
        try {
            if (loadLocation.contains(" ")) {
                String[] params = loadLocation.split(" ");
                String directory = params[0].toLowerCase();
                String fileName = params[1];
                storage.loadFileWithDirectory(directory, fileName);
            } else {
                storage.loadFileWithFileName(loadLocation);
            }
            
            mainList = storage.getMainList();
            doneList = storage.getCompletedList();

            /// post-processing
            sortList(mainList);
            updateWeekList();
            updateFileDictionary(loadLocation);
            updateTaskProgress();
            canUndo = false;
            canRedo = false;
            
            return new Result(CommandType.LOAD, true, "Loaded " + loadLocation, weekList);
        } catch (IOException | ParseException ioe) {
            mainList = tempMainList;
            doneList = tempDoneList;
            canUndo = false;
            canRedo = false;
            return new Result(CommandType.LOAD, false, "Failed to load " + loadLocation, weekList);
        }
    }
    
    // Task retrieval commands
    
    public Result searchTasks(Command command) {
        // pre-processing
        clearModifiedStatus();
        canUndo = false;
        canRedo = false;
        
        ArrayList<String> categories = command.getCategories();
        if (categories != null) {
            return searchTasksByCategory(categories);
        }
        LocalDateTime start = command.getStartDate();
        LocalDateTime end = command.getEndDate();
        if (start != null || end != null) {
            return searchTasksByDate(start, end);
        }
        String keyword = command.getDescription();
        if (keyword == null || keyword.trim().isEmpty()) {
            return new Result(CommandType.SEARCH, true, "Showing all tasks", mainList);
        }
        return searchTasksByKeyword(keyword);
    }

    private Result searchTasksByCategory(ArrayList<String> categories) {
        ArrayList<Task> searchResults = new ArrayList<Task>();
        for (Task task : mainList) {
            boolean put = true;
            for (String category : categories) {
                category = toSentenceCase(category);
                if (!task.getCategories().contains(category)) {
                    put = false;
                    break;                      
                }
            }
            if (put) {
                searchResults.add(task);                    
            }
        }
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }

    private Result searchTasksByDate(LocalDateTime start, LocalDateTime end) {
        ArrayList<Task> searchResults = new ArrayList<Task>();
        if (start == null) { // search by end time (search backward)
            for (Task task: mainList) {
                LocalDateTime taskEnd = task.getEnd();
                if (taskEnd.compareTo(end) < 0) {
                    searchResults.add(task);
                }
            }
        } else if (end == null) { // search by start time (search forward)
            for (Task task: mainList) {
                LocalDateTime taskStart = task.getStart();
                if (taskStart.compareTo(start) > 0) {
                    searchResults.add(task);
                }
            }
            
        } /*else { // search by a range of time (the range)
            for (Task task: mainList) {
                LocalDateTime taskStart = task.getStart();
                LocalDateTime taskEnd = task.getEnd();
                if (taskStart.isAfter())
            }
        }*/
        
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }

    private Result searchTasksByKeyword(String keyword) {
        ArrayList<Task> searchResults = new ArrayList<Task>();
        for (Task task : mainList) {
            String descriptionLowerCase = task.getDescription().toLowerCase();
            String keywordLowerCase = keyword.toLowerCase();
            if (descriptionLowerCase.contains(keywordLowerCase)) {
                searchResults.add(task);
            }
        }
        
        updateDictionary(keyword);
        
        return new Result(CommandType.SEARCH, true, "Searched", searchResults);
    }

    public Result filterTasks() {
        return new Result(Command.CommandType.HOME, true, "Return home", weekList);
    }
    
    // Task details and progress
    
    private void setStartEnd(LocalDateTime start, LocalDateTime end, Task newTask) {
        newTask.setStart(start);
        newTask.setEnd(end);
    }
    
    private void setCategories(ArrayList<String> inputCategories, Task newTask) {
        if (inputCategories != null) {
            ArrayList<String> list = new ArrayList<String>();
            for (String cat : inputCategories) {
                if (cat.equalsIgnoreCase(CATEGORY_PRIORITY)) {
                    newTask.setImportance(true);
                    // update the number of task in priority category
                    Iterator<Entry<String, Integer>> iterator = categories.iterator();
                    updateNumOfTaskForPriority(iterator);

                } else {
                    list.add(toSentenceCase(cat));
                    // check if the category is previously added
                    boolean isAdded = false;
                    Iterator<Entry<String, Integer>> iterator = categories.iterator();
                    isAdded = updateNumOfTaskForCat(cat, isAdded, iterator);

                    // First time the user creates this category
                    if (!isAdded) {
                        categories.add(new AbstractMap.SimpleEntry<String, Integer>(cat, 1));
                    }
                }
            }
            newTask.setCategories(list);
        }
    }
    
    private boolean updateNumOfTaskForCat(String cat, boolean isAdded, Iterator<Entry<String, Integer>> iterator) {
        int numOfTasks;
        while (iterator.hasNext()) {
            Entry<String, Integer> next = iterator.next();
            if (next.getKey().equalsIgnoreCase(cat)) {
                numOfTasks = next.getValue();
                int updateNum = numOfTasks + 1;
                next.setValue(updateNum);
                isAdded = true;
                break;
            }
        }
        return isAdded;
    }

    private void updateNumOfTaskForPriority(Iterator<Entry<String, Integer>> iterator) {
        int numOfTasks;
        while (iterator.hasNext()) {
            Entry<String, Integer> next = iterator.next();
            if (next.getKey().equalsIgnoreCase(CATEGORY_PRIORITY)) {
                numOfTasks = next.getValue();
                int updateNum = numOfTasks + 1;
                next.setValue(updateNum);
                break;
            }
        }
    }

    public void updateTaskProgress() {
        categories.clear();
        categories.add(new AbstractMap.SimpleEntry<String, Integer>(CATEGORY_PRIORITY, 0));
        categories.add(new AbstractMap.SimpleEntry<String, Integer>(CATEGORY_TODAY, 0));
        LocalDate currentDate = LocalDate.now();

        for (Task task : mainList) {
            if (task.isImportant()) {
                // update 'Priority' category count
                int count = removeFromDictionary(categories, CATEGORY_PRIORITY);
                addToDictionary(categories, CATEGORY_PRIORITY, ++count);
            }

            if (task.isOccurringOn(currentDate)) {
                // update 'Today' category count
                int count = removeFromDictionary(categories, CATEGORY_TODAY);
                addToDictionary(categories, CATEGORY_TODAY, ++count);
            }

            // Other categories
            for (String cat : task.getCategories()) {
                int count = removeFromDictionary(categories, cat);
                addToDictionary(categories, cat, ++count);
            }

        }
    }

    /*****************************************
     * METHODS FOR AUTOCOMPLETION DICTIONARY *
     *****************************************/
    
    private void updateDictionary(String text) {
        assert (text != null);
        text = text.trim();
        if (!text.isEmpty()) {
            updateTaskDictionary(text);
            updateWordDictionary(text);
            saveAutoCompletionList();
        }
    }

    // @@author Ruoling
    private void saveAutoCompletionList() {
        // convert TreeSet to ArrayList
        ArrayList<String> autoCompletionList = new ArrayList<String>();
        for (Entry<String, Integer> entry : taskDictionary) {
            autoCompletionList.add(entry.getKey());
        }
        storage.setAutoCompletionList(autoCompletionList);
        try {
            storage.writeAutoCompletionData();
        } catch (IOException ioe) {
            // do nothing here
        }
    }

    private void updateTaskDictionary(String text) {
        int freqCount = removeFromDictionary(taskDictionary, text);
        addToDictionary(taskDictionary, text, ++freqCount);
    }

    private void updateWordDictionary(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!isNumberOrFunctionWord(words[i])) {
                int freqCount = removeFromDictionary(wordDictionary, words[i]);
                addToDictionary(wordDictionary, words[i], ++freqCount);
            }
        }
    }

    private void updateFileDictionary(String text) {
        int freqCount = removeFromDictionary(fileDictionary, text);
        addToDictionary(fileDictionary, text, ++freqCount);
    }

    // Remove the text from the dictionary if it exists and return its frequency
    // count
    // If it does not already exist in the dictionary, frequency count is 0
    private int removeFromDictionary(TreeSet<Entry<String, Integer>> dictionary, String text) {
        int freqCount = 0;
        Iterator<Entry<String, Integer>> iterator = dictionary.iterator();
        while (iterator.hasNext()) {
            Entry<String, Integer> next = iterator.next();
            if (next.getKey().equalsIgnoreCase(text)) {
                // Keep the frequency count and remove the entry
                freqCount = next.getValue();
                iterator.remove();
                break;
            }
        }
        return freqCount;
    }

    // Add the text to the dictionary with its frequency count
    private void addToDictionary(TreeSet<Entry<String, Integer>> dictionary, String text, int freqCount) {
        dictionary.add(new AbstractMap.SimpleEntry<String, Integer>(text, freqCount));
    }

    // Returns true if the specified word is a number or function word
    private boolean isNumberOrFunctionWord(String word) {
        // Check if word contains only numbers and the decimal separator
        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return true;
        }
        // Check if word is a pre-defined function word
        // index will be >= 0 if it is
        String wordLowerCase = word.toLowerCase();
        int index = Arrays.binarySearch(functionWords, wordLowerCase, String.CASE_INSENSITIVE_ORDER);
        return index >= 0 ? true : false;
    }

    // @@author Gilbert
    /******************
     * GETTER METHODS *
     ******************/

    public ArrayList<Task> getMainList() {
        return mainList;
    }

    public ArrayList<Task> getDoneList() {
        return doneList;
    }

    public TreeSet<Entry<String, Integer>> getCategories() {
        return categories;
    }

    public ArrayList<Task> getPreviousList() {
        return previousCopyOfMainList;
    }
    
    public ArrayList<Task> getWeekList() {
        return weekList;
    }

    public TreeSet<Entry<String, Integer>> getTaskDictionary() {
        return taskDictionary;
    }

    public TreeSet<Entry<String, Integer>> getWordDictionary() {
        return wordDictionary;
    }

    public TreeSet<Entry<String, Integer>> getFileDictionary() {
        return fileDictionary;
    }
    
    /******************
     * HELPER METHODS *
     ******************/

    public void setMainList(ArrayList<Task> mainList) {
        Execution.mainList = mainList;
        sortList(mainList);
    }

    public void sortList(ArrayList<Task> thisList) {
        Collections.sort(thisList);
        int id = 1;
        for (Task task : thisList) {
            task.setId(id);
            id++;
        }    
    }
    
    public void updateWeekList() {
        weekList.clear();
        
        LocalDateTime today = LocalDateTime.now();
        LocalDate todayDate = LocalDate.now();
        LocalDate weekAfterToday = todayDate.plusWeeks(1);
        
        for (Task task : mainList) {
            if (task.isFloating()) {
                weekList.add(task);
            } else if (task.isOverdue(today)) {
                weekList.add(task);
            } else {
                LocalDate taskEndDate = task.getEndDate();
                if (taskEndDate.isBefore(weekAfterToday)) {
                    weekList.add(task);
                }
            }
        }    }

    // Sets the status of all tasks to be unmodified
    private void clearModifiedStatus() {
        for (Task task : mainList) {
            task.setModified(false);
        }
    }
    
    private boolean isValidIndex(int index) {
        int maxIndex = mainList.size() - 1;
        if (index < 0 || index > maxIndex) {
            return false;
        }
        return true;
    }
    
    public void saveMainListForUndo() {
        previousCopyOfMainList.clear();
        previousCopyOfMainList.addAll(mainList);
    }

    public void saveDoneListForUndo() {
        previousCopyOfDoneList.clear();
        previousCopyOfDoneList.addAll(doneList);
    }
   
    private void saveMainList(ArrayList<Task> thisList) throws IOException {
        storage.setMainList(thisList);
        storage.writeToFile();
    }

    private void saveMainAndDoneList(ArrayList<Task> mainList, ArrayList<Task> doneList)
            throws IOException {
        storage.setMainList(mainList);
        storage.setCompletedList(doneList);
        storage.writeToFile();
    }

    /**
     * Formats a string to sentence case.
     * Leading and trailing whitespace will be removed. The first non-whitespace character
     * is capitalised and the remaining text is converted to lower case
     * 
     * @param text  String to format
     * @return      String formatted in sentence case
     */
    public String toSentenceCase(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        String textTrimmed = text.trim();
        if (textTrimmed.isEmpty()) {
            return textTrimmed;
        }
        String sentenceCase = textTrimmed.substring(0, 1).toUpperCase();
        if (textTrimmed.length() > 1) {
            sentenceCase += textTrimmed.substring(1).toLowerCase();
        }
        System.out.println(sentenceCase);
        return sentenceCase;
            
    }

}
