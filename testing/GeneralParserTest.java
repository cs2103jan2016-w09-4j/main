//@@author Khanh
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import gridtask.common.Command;
import gridtask.common.Command.CommandType;
import gridtask.parser.GeneralParser;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class GeneralParserTest {

    @Test
    public void parseCommand_AddWithDescriptionOnly_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "a task with no time");
        try {
            Command actual = parser.parseCommand("add a task with no time");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_NoTime_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(0, 0)),
                null, new ArrayList<String>());

        try {
            Command actual = parser.parseCommand("add eat start 20-10-2010");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_AddWithEnd_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(22, 30)), null, null);

        try {
            Command actual = parser.parseCommand("add eat start 22:30 20-10-2010");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_AddWithTime_DefaultToday() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)),
                                                                LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)),
                                                                new ArrayList<String>());

        try {
            Command actual = parser.parseCommand("add eat start 4pm end 5pm");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_AddWithStartAndEnd_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat more food to get fat",
                LocalDateTime.of(LocalDate.of(2000, 10, 20), LocalTime.of(22, 30)),
                LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)), null);

        try {
            Command actual = parser.parseCommand("add eat more food to get fat start 22:30 20-10-2000 end 9:10 29-8-2004");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_SaveNoDirectory_SaveCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.SAVE);

        try {
            Command actual = parser.parseCommand("save");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parse_SimpleAdd_DescriptionParser(){

    }
}
