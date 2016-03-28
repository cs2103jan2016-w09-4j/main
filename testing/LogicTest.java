import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import common.*;
import common.Command.CommandType;
import logic.Logic;

public class LogicTest{
    
    Logic logic = Logic.getInstance();
    ArrayList<Task> mainList = new ArrayList<Task>();
    ArrayList<Task> compareList = new ArrayList<Task>();
    
    /*
    @Test
    public void addTask_MissingDescription_InvalidCommand() {
        Result result1 = logic.processCommand("add");
        assertEquals(Command.CommandType.INVALID, result1.getCommandType());
        
        Result result2 = logic.processCommand("add ");
        assertEquals(Command.CommandType.INVALID, result2.getCommandType());
        
        Result result3 = logic.processCommand("add end 31/12/2016 12:00");
        assertEquals(Command.CommandType.INVALID, result3.getCommandType());
    }
    
    @Test
    public void addTask_HaveStartButNoEnd_InvalidCommand() {
        Result result1 = logic.processCommand("add 111 start 01/01/2016 12:00");
        assertEquals(Command.CommandType.INVALID, result1.getCommandType());
    }
    */

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
        logic.processCommand("add Meeting start 24/05/2016 12:00 end 26/05/2016 14:00");
        mainList = logic.getMainList();
        
        compareList = new ArrayList<Task>();
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 24, 14, 0), 1));
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 25, 14, 0), 2));
        compareList.add(new Task("Meeting", LocalDateTime.of(2016, 05, 24, 12, 0), LocalDateTime.of(2016, 05, 26, 14, 0), 3));
        compareList.add(new Task("Goodbye", null, null, 4));
        compareList.add(new Task("Hello", null, null, 5));
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    public void editTask_AllTaskTypes_EditedTheSelectedTask() {
    	mainList = logic.getMainList();
    	logic.processCommand("edit 5 Hello World");
    	mainList = logic.getMainList();
    	
    	compareList = new ArrayList<Task>();
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 24, 14, 0), 1));
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 25, 14, 0), 2));
        compareList.add(new Task("Meeting", LocalDateTime.of(2016, 05, 24, 12, 0), LocalDateTime.of(2016, 05, 26, 14, 0), 3));
        compareList.add(new Task("Goodbye", null, null, 4));
        compareList.add(new Task("Hello", null, null, 5));
        
        compareList.get(4).setDescription("Hello World");
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    public void deleteTask_AllTaskTypes_DeletedTheSelectedTask() {
    	mainList = logic.getMainList();
    	logic.processCommand("delete 5");
    	mainList = logic.getMainList();
    	
    	compareList = new ArrayList<Task>();
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 24, 14, 0), 1));
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 25, 14, 0), 2));
        compareList.add(new Task("Meeting", LocalDateTime.of(2016, 05, 24, 12, 0), LocalDateTime.of(2016, 05, 26, 14, 0), 3));
        compareList.add(new Task("Goodbye", null, null, 4));
        compareList.add(new Task("Hello World", null, null, 5));
        
        compareList.remove(4);
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }

    public void saveTasks_WithFileName_Success() {
        Result result = logic.processCommand("save logic_testFile1.txt");
        assertEquals(Command.CommandType.SAVE, result.getCommandType());
        assertEquals(true, result.isSuccess());
    }

    public void loadTasks_WithFileName_Success() {
        logic.processCommand("load logic_testFile1.txt");
        mainList = logic.getMainList();
        
        compareList = new ArrayList<Task>();
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 24, 14, 0), 1));
        compareList.add(new Task("Meeting", null, LocalDateTime.of(2016, 05, 25, 14, 0), 2));
        compareList.add(new Task("Meeting", LocalDateTime.of(2016, 05, 24, 12, 0), LocalDateTime.of(2016, 05, 26, 14, 0), 3));
        compareList.add(new Task("Goodbye", null, null, 4));
        //compareList.add(new Task("Hello World", null, null, 5));
        assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    /*
    @Test
    public void testOne(){  
        
        logic.processCommand("add hello");
        logic.processCommand("add goodbye");
        logic.processCommand("add meeting 2pm");
        logic.processCommand("delete 2");
        logic.processCommand("save logic_testFile1.txt");
        mainList = logic.getMainList();
        compareList = logic.processCommand("load logic_expected1.txt").getResults();
        assertArrayEquals(mainList.toArray(), compareList.toArray());
        mainList.clear();
        compareList.clear();
    }
    
    @Test
    public void testTwo(){
        
        logic.processCommand("add 123456");
        logic.processCommand("add take me to the top");
        logic.processCommand("add hello there");
        logic.processCommand("add myself; yourself");
        logic.processCommand("edit 1 used to be numbers here");
        logic.processCommand("undo");
        logic.processCommand("save logic_testFile2.txt");
        mainList = logic.getMainList();
        compareList = logic.processCommand("load logic_expected2.txt").getResults();
        assertArrayEquals(mainList.toArray(), compareList.toArray());
        mainList.clear();
        compareList.clear();
    }
    */    
}
