import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.Test;

import gridtask.parser.TaskDetails;

public class TaskDetailsTest {
    @Test
    public void TaskDetails_AddWithDescription() {

        TaskDetails expected = new TaskDetails("a task with no time", null, null, new ArrayList<String>());
        try {
            TaskDetails actual = new TaskDetails("a task with no time");
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartTime(), actual.getStartTime());
            assertEquals(expected.getEndTime(), actual.getEndTime());
            assertEquals(expected.getCategories(), actual.getCategories());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void TaskDetails_TaskWithStartDate_ReturnDefaultTime() {
        TaskDetails expected = new TaskDetails("eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(0, 0)),
                null, new ArrayList<String>());

        try {
            TaskDetails actual = new TaskDetails("eat start 20-10-2010");
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartTime(), actual.getStartTime());
            assertEquals(expected.getEndTime(), actual.getEndTime());
            assertEquals(expected.getCategories(), actual.getCategories());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void TaskDetails_HasEndTime() {
        TaskDetails expected = new TaskDetails("eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(22, 30)), null,
                new ArrayList<String>());

        try {
            TaskDetails actual = new TaskDetails("eat start 22:30 20-10-2010");
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartTime(), actual.getStartTime());
            assertEquals(expected.getEndTime(), actual.getEndTime());
            assertEquals(expected.getCategories(), actual.getCategories());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void TaskDetails_OnlyTimeNoDate_DefaultToday() {
        TaskDetails expected = new TaskDetails("eat", LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)),
                                                        LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)),
                                                        new ArrayList<String>());

        try {
            TaskDetails actual = new TaskDetails("eat start 4pm end 5pm");
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartTime(), actual.getStartTime());
            assertEquals(expected.getEndTime(), actual.getEndTime());
            assertEquals(expected.getCategories(), actual.getCategories());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void TaskDetails_HasCategories() {
        ArrayList<String> expectedCategories = new ArrayList<String>();
        expectedCategories.add("work");
        expectedCategories.add("fun");
        TaskDetails expected = new TaskDetails("go to school", null, null, expectedCategories);

        try {
            TaskDetails actual = new TaskDetails("go to school #work #fun");
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartTime(), actual.getStartTime());
            assertEquals(expected.getEndTime(), actual.getEndTime());
            assertEquals(expected.getCategories(), actual.getCategories());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }
}
