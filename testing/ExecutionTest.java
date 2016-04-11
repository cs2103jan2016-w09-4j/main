//@@author A0123972A
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.Test;

import gridtask.common.*;
import gridtask.common.Command.CommandType;
import gridtask.logic.Execution;
import gridtask.logic.Logic;

public class ExecutionTest{
	
	Logic logic = Logic.getInstance();
	Execution execution = new Execution();
	
    ArrayList<Task> mainList = new ArrayList<Task>();
    ArrayList<Task> compareList = new ArrayList<Task>();;
    
    private static final String CATEGORY_PRIORITY = "Priority";
    private static final String CATEGORY_TODAY = "Today";
    
    @Test
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
    

	
    @Test
    public void deleteTask_AllTaskTypes_DeletedTheSelectedTask() {
    	
    	execution.deleteTask(new Command(CommandType.DELETE, 1));
    	mainList = execution.getMainList();
    	
    	compareList.add(new Task("Only end time", null, LocalDateTime.of(2016, 05, 24, 18, 0), 1));
    	compareList.add(new Task("Start and end time", LocalDateTime.of(2016, 5, 24, 14, 0), LocalDateTime.of(2016, 5, 24, 18, 0), 2));
    	compareList.add(new Task("Hello", null, null, 3));
    	compareList.add(new Task("Testing", null, null, 4));
    	compareList.remove(0);
    	
    	int index = 1;
    	for (Task task : compareList) {
    	    task.setId(index++);
    	}
    	assertArrayEquals(compareList.toArray(), mainList.toArray());
    }
    
    @Test
    public void updateTaskProgress_AllCategoryTypes_SuccessfullyUpdated() {
    	ArrayList<String> cat = new ArrayList<String>();
    	cat.add(CATEGORY_TODAY);
    	execution.addTask(new Command(CommandType.ADD, "Update task progress", null, null, cat));
    	execution.updateTaskProgress();
    	int categoryCount = execution.getCategoryCount(CATEGORY_TODAY);
    	assertEquals(categoryCount, 1);
    	
    	ArrayList<String> cate = new ArrayList<String>();
    	cate.add(CATEGORY_PRIORITY);
    	execution.addTask(new Command(CommandType.ADD, "Update task progress", null, null, cate));
    	execution.updateTaskProgress();
    	int cateCount = execution.getCategoryCount(CATEGORY_PRIORITY);
    	assertEquals(cateCount, 1);
    	
    	ArrayList<String> ct = new ArrayList<String>();
    	ct.add("hello");
    	execution.addTask(new Command(CommandType.ADD, "Final add", null, null, ct));
    	execution.addTask(new Command(CommandType.ADD, "Final add +1", null, null, ct));
    	execution.updateTaskProgress();
    	int ctCount = execution.getCategoryCount("hello");
    	assertEquals(ctCount, 2);
    }
    
}