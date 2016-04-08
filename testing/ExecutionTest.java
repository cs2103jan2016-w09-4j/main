import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import common.*;
import common.Command.CommandType;
import junitx.framework.FileAssert;
import logic.Logic;
import logic.Execution;

public class ExecutionTest{
	
	Logic logic = Logic.getInstance();
	Execution execution = logic.getExecutionInstance();
	
    ArrayList<Task> mainList = new ArrayList<Task>();
    ArrayList<Task> compareList = new ArrayList<Task>();;
    
    @Test
    public void testUserSessionOne() {
     //   loadTasks_WithFileName_Success();
        addTask_AllTaskTypes_AddedInOrder();
        editTask_AllTaskTypes_EditedTheSelectedTask();
        deleteTask_AllTaskTypes_DeletedTheSelectedTask();
        doneTask_AllTaskTypes_DoneTheSelectedTask();
      //  saveTasks_WithFileName_Success();
      //  loadTasks_WithFileName_Success();
    }
    
    public void addTask_AllTaskTypes_AddedInOrder() {
    	
    	execution.setMainList(mainList);
    	
    	LocalDateTime start = LocalDateTime.of(2016, 5, 24, 14, 0);  // 24/5/2016 2pm
    	LocalDateTime end = LocalDateTime.of(2016, 5, 24, 18, 0);  // 24/5/2016 6pm
    	
    	execution.addTask(new Command(CommandType.ADD, "Hello"));
    	execution.addTask(new Command(CommandType.ADD, "Testing"));
    	execution.addTask(new Command(CommandType.ADD, "Only end time", null, end, null));
    	execution.addTask(new Command(CommandType.ADD, "Start and end time", start, end, null));
    	mainList = execution.getMainList();

    	compareList = new ArrayList<Task>();
    	compareList.add(new Task("Only end time", null, LocalDateTime.of(2016, 05, 24, 18, 0), 1));
    	compareList.add(new Task("Start and end time", LocalDateTime.of(2016, 5, 24, 14, 0), LocalDateTime.of(2016, 5, 24, 18, 0), 2));
    	compareList.add(new Task("Hello", null, null, 3));
    	compareList.add(new Task("Testing", null, null, 4));
    	
    	assertArrayEquals(compareList.toArray(), mainList.toArray());
    	
    }
    
    public void editTask_AllTaskTypes_EditedTheSelectedTask() {
    	
    	execution.editTask(new Command(CommandType.EDIT, 3, "Sayonara no sora", null, null, null));
    	mainList = execution.getMainList();
    	
    	compareList.get(2).setDescription("Sayonara no sora");
    	assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
	
    public void deleteTask_AllTaskTypes_DeletedTheSelectedTask() {
    	
    	execution.deleteTask(new Command(CommandType.DELETE, 1));
    	mainList = execution.getMainList();
    	
    	compareList.remove(0);
    	int index = 1;
    	for (Task task : compareList) {
    	    task.setId(index++);
    	}
    	assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    public void doneTask_AllTaskTypes_DoneTheSelectedTask() {
    	
    	execution.doneTask(new Command(CommandType.DONE, 1));
    	mainList = execution.getDoneList();
    	
    	ArrayList<Task> done = new ArrayList<Task>();
    	done.add(compareList.remove(0));
    	assertArrayEquals(done.toArray(), mainList.toArray());
    }
    
    
	
}
