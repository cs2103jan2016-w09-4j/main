import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import common.*;
import junitx.framework.FileAssert;
import logic.Logic;

public class LogicTest {
    
    public static final String DEFAULT_FILE_NAME = "MyTest.txt";
    
    Logic logic = Logic.getInstance();
    ArrayList<Task> mainList = new ArrayList<Task>();
    ArrayList<Task> doneList = new ArrayList<Task>();
    ArrayList<Task> searchList = new ArrayList<Task>();
    ArrayList<Task> compareList = new ArrayList<Task>();

    @Test
    public void addTask_MissingDescription_InvalidCommand() {
        Result noSpace = logic.processCommand("add");
        assertEquals(Command.CommandType.ADD, noSpace.getCommandType());
        assertEquals(false, noSpace.isSuccess());
        assertTrue(noSpace.getResults().isEmpty());
        
        Result noDescAfterSpace = logic.processCommand("add  ");
        assertEquals(Command.CommandType.ADD, noDescAfterSpace.getCommandType());
        assertEquals(false, noSpace.isSuccess());
        assertTrue(noDescAfterSpace.getResults().isEmpty());
        
        Result noDescWithDate = logic.processCommand("add end today");
        assertEquals(Command.CommandType.ADD, noDescWithDate.getCommandType());
        assertEquals(false, noSpace.isSuccess());
        assertTrue(noDescWithDate.getResults().isEmpty());
    }
    
    @Test
    public void addTask_InvalidDate_InvalidCommand() {
        Result laterStart = logic.processCommand("add 111 start 01/01/2016 12:00 end 31/12/2015 12:00");
        assertEquals(Command.CommandType.INVALID, laterStart.getCommandType());
    }

    // Test a single user session: simple CRUD commands, saving and loading 
    @Test
    public void testUserSessionOne() throws IOException {
        createEmptyFile(DEFAULT_FILE_NAME);
        logic.processCommand("load " + DEFAULT_FILE_NAME);
        compareList = new ArrayList<Task>();

        addTask_AllTaskTypes_AddedInOrder();
        editTask_AllTaskTypes_EditedTheSelectedTask();
        deleteTask_AllTaskTypes_DeletedTheSelectedTask();
        saveTasks_WithFileName_Success();
        loadTasks_WithFileName_Success();
    }
    
    public void addTask_AllTaskTypes_AddedInOrder() {
        logic.processCommand("add Hello");
        logic.processCommand("add Goodbye");
        logic.processCommand("add Meeting end 25/05/2016 14:00");
        logic.processCommand("add Meeting end 24/05/2016 14:00");
        Result result = logic.processCommand("add Meeting start 24/05/2016 12:00 end 26/05/2016 14:00");
        mainList = result.getResults();
        
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 24, 14, 0), 1));
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 25, 14, 0), 2));
        compareList.add(new Task("Meeting", LocalDateTime.of(2016, 05, 24, 12, 0), LocalDateTime.of(2016, 05, 26, 14, 0), 3));
        compareList.add(new Task("Goodbye", null, null, 4));
        compareList.add(new Task("Hello", null, null, 5));
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    public void editTask_AllTaskTypes_EditedTheSelectedTask() {
        Result result = logic.processCommand("edit 5 Hello World");
    	mainList = result.getResults();

    	compareList.get(4).setDescription("Hello World");
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    public void deleteTask_AllTaskTypes_DeletedTheSelectedTask() {
        Result result = logic.processCommand("delete 5");
        mainList = result.getResults();
        
    	compareList.remove(4);
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    public void saveTasks_WithFileName_Success() {
        Result result = logic.processCommand("save logic_output1.txt");
        assertEquals(Command.CommandType.SAVE, result.getCommandType());
        assertEquals(true, result.isSuccess());
        File expectedFile = new File("logic_expected1.txt");
        File actualFile = new File("logic_output1.txt");
        FileAssert.assertEquals(expectedFile, actualFile);
    }

    public void loadTasks_WithFileName_Success() {
        Result result = logic.processCommand("load logic_output1.txt");
        mainList = result.getResults();
        assertEquals(Command.CommandType.LOAD, result.getCommandType());
        assertEquals(true, result.isSuccess());
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    // Test a single user session: search, done and searchdone
    @Test
    public void testUserSessionTwo() throws IOException {
        logic.processCommand("load logic_input2.txt");
        logic.processCommand("save logic_output2.txt");
        mainList = logic.getMainList();
        compareList = new ArrayList<Task>();
        doneList = new ArrayList<Task>();
        doneList.add(new Task("CCCCC AAAAA"));
        doneList.add(mainList.get(3));
        doneList.add(mainList.get(2));
        doneList.add(mainList.get(0));
        
        search_NoParam_DisplayAll();
        search_Keyword_DisplayMatching();
        search_Date_DisplayMatching();
        search_Category_DisplayMatching();
        done_ValidTaskNumber_MarkAsDone();
        done_InvalidTaskNumber_NoChange();
        searchDone_NoParam_DisplayAll();
        searchDone_Keyword_DisplayMatching();
        searchDone_Date_DisplayMatching();
        searchDone_Category_DisplayMatching();
    }
    
    private void search_NoParam_DisplayAll() {
        compareList.clear();
        compareList.addAll(mainList);
        
        Result noSpace = logic.processCommand("search");
        searchList = noSpace.getResults();
        assertEquals(Command.CommandType.SEARCH, noSpace.getCommandType());
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result hasWhitespace = logic.processCommand("search  ");
        searchList = hasWhitespace.getResults();
        assertEquals(Command.CommandType.SEARCH, hasWhitespace.getCommandType());
        assertArrayEquals(compareList.toArray(), searchList.toArray());
    }

    private void search_Keyword_DisplayMatching() {
        Result word = logic.processCommand("search aAA");
        searchList = word.getResults();
        compareList.clear();
        compareList.add(mainList.get(0));
        compareList.add(mainList.get(1));
        compareList.add(mainList.get(2));
        compareList.add(mainList.get(3));
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result wordWithTrailingWhitespace = logic.processCommand("search aAA   ");
        searchList = wordWithTrailingWhitespace.getResults();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result phrase = logic.processCommand("search aAA bBB");
        searchList = phrase.getResults();
        compareList.clear();
        compareList.add(mainList.get(1));
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result phraseWithTrailingWhitespace = logic.processCommand("search aAA bBB  ");
        searchList = phraseWithTrailingWhitespace.getResults();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result invalid = logic.processCommand("search cCC");
        searchList = invalid.getResults();
        compareList.clear();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
    }

    private void search_Date_DisplayMatching() {
        Result valid = logic.processCommand("search 01/01/2016");
        searchList = valid.getResults();
        compareList.clear();
        compareList.add(mainList.get(0));
        compareList.add(mainList.get(1));
        compareList.add(mainList.get(3));
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result invalid = logic.processCommand("search 01/01/2015");
        searchList = invalid.getResults();
        compareList.clear();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
    }

    private void search_Category_DisplayMatching() {
        // TODO Auto-generated method stub
        
    }

    private void done_ValidTaskNumber_MarkAsDone() {
        logic.processCommand("done 4");
        logic.processCommand("done 3");
        logic.processCommand("done 1");
        
        File expectedFile = new File("logic_expected2.txt");
        File actualFile = new File("logic_output2.txt");
        FileAssert.assertEquals(expectedFile, actualFile);
    }

    private void done_InvalidTaskNumber_NoChange() {
        Result negative = logic.processCommand("done -1");
        assertEquals(Command.CommandType.DONE, negative.getCommandType());
        assertEquals(false, negative.isSuccess());
        
        Result outOfRange = logic.processCommand("done 10");
        assertEquals(Command.CommandType.DONE, outOfRange.getCommandType());
        assertEquals(false, outOfRange.isSuccess());
        
        Result nonInteger = logic.processCommand("done 1.0");
        assertEquals(Command.CommandType.DONE, nonInteger.getCommandType());
        assertEquals(false, nonInteger.isSuccess());
        
        File expectedFile = new File("logic_expected2.txt");
        File actualFile = new File("logic_output2.txt");
        FileAssert.assertEquals(expectedFile, actualFile);
    }

    private void searchDone_NoParam_DisplayAll() {
        // TODO Auto-generated method stub
        compareList.clear();
        
        Result noSpace = logic.processCommand("search");
        searchList = noSpace.getResults();
        assertEquals(Command.CommandType.SEARCH, noSpace.getCommandType());
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result hasWhitespace = logic.processCommand("search  ");
        searchList = hasWhitespace.getResults();
        assertEquals(Command.CommandType.SEARCH, hasWhitespace.getCommandType());
        assertArrayEquals(compareList.toArray(), searchList.toArray());
    }

    private void searchDone_Keyword_DisplayMatching() {
        Result word = logic.processCommand("searchdone aAA");
        searchList = word.getResults();
        compareList.clear();
        compareList.add(doneList.get(0));
        compareList.add(doneList.get(1));
        compareList.add(doneList.get(2));
        compareList.add(doneList.get(3));
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result wordWithTrailingWhitespace = logic.processCommand("searchdone aAA   ");
        searchList = wordWithTrailingWhitespace.getResults();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result phrase = logic.processCommand("search cCC aAA");
        searchList = phrase.getResults();
        compareList.clear();
        compareList.add(doneList.get(0));
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result phraseWithTrailingWhitespace = logic.processCommand("search aAA bBB  ");
        searchList = phraseWithTrailingWhitespace.getResults();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
        
        Result invalid = logic.processCommand("search bBB");
        searchList = invalid.getResults();
        compareList.clear();
        assertArrayEquals(compareList.toArray(), searchList.toArray());
    }

    private void searchDone_Date_DisplayMatching() {
        // TODO
        Result existingDate = logic.processCommand("searchdone 12/12/2016");
    }

    private void searchDone_Category_DisplayMatching() {
        // TODO
        Result lowerCase = logic.processCommand("searchdone #priority");
        Result upperCase = logic.processCommand("searchdone #PRIORITY");
    }

    // Test a single user session: undo and redo
    @Test
    public void testUserSessionThree() {
        logic.processCommand("load logic_expected3.txt");
        logic.processCommand("save logic_output3.txt");
        compareList = new ArrayList<Task>();
        compareList.add(new Task("Macademia Nut Cookie", 1));
        compareList.add(new Task("New York Cheesecake", 2));
        
        undo_AfterEdit_ReturnToPreviousState();
        redo_AfterEdit_ReturnToCurrentState();
        undo_AfterDelete_ReturnToPreviousState();
        redo_AfterDelete_ReturnToCurrentState();
        undo_AfterAdd_ReturnToPreviousState();
        redo_AfterAdd_ReturnToCurrentState();
        undo_AfterUnallowedCommand_NoChange();
        redo_AfterUnallowedCommand_NoChange();
        
        File expectedFile = new File("logic_expected3.txt");
        File actualFile = new File("logic_output3.txt");
        FileAssert.assertEquals(expectedFile, actualFile);
    }

    private void undo_AfterEdit_ReturnToPreviousState() {
        logic.processCommand("edit 1 Marshmallow");
        Result result = logic.processCommand("undo");
        mainList = result.getResults();
        
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    private void redo_AfterEdit_ReturnToCurrentState() {
        Result result = logic.processCommand("redo");
        mainList = result.getResults();
        
        compareList.get(0).setDescription("Marshmallow");
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    private void undo_AfterDelete_ReturnToPreviousState() {
        logic.processCommand("delete 1");
        Result result = logic.processCommand("undo");
        mainList = result.getResults();
        
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    private void redo_AfterDelete_ReturnToCurrentState() {
        Result result = logic.processCommand("redo");
        mainList = result.getResults();
        
        compareList.get(1).setId(1);
        compareList.remove(0);
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    private void undo_AfterAdd_ReturnToPreviousState() {
        logic.processCommand("add Macademia Nut Cookie");
        Result result = logic.processCommand("undo");
        mainList = result.getResults();

        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    private void redo_AfterAdd_ReturnToCurrentState() {
        Result result = logic.processCommand("redo");
        mainList = result.getResults();
        
        compareList.add(0, new Task("Macademia Nut Cookie", 1));
        compareList.get(1).setId(2);
        assertArrayEquals(compareList.toArray(), mainList.toArray());        
    }

    private void undo_AfterUnallowedCommand_NoChange() {
        Result result = logic.processCommand("undo");
        assertEquals(Command.CommandType.UNDO, result.getCommandType());
        assertEquals(false, result.isSuccess());
    }

    private void redo_AfterUnallowedCommand_NoChange() {
        Result result = logic.processCommand("redo");
        assertEquals(Command.CommandType.REDO, result.getCommandType());
        assertEquals(false, result.isSuccess());
    }
    
    private static void createEmptyFile(String fileName) throws IOException {
        File file = new File(fileName);
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(file))) {
            buffWriter.write("");
        }
    }
}
