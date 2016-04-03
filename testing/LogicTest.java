import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import common.*;
import junitx.framework.FileAssert;
import logic.Logic;

public class LogicTest {
    
    Logic logic = Logic.getInstance();
    ArrayList<Task> mainList = new ArrayList<Task>();
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
    public void testUserSessionOne() {
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
        
        compareList = new ArrayList<Task>();
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

}
