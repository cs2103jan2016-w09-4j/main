import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import common.Command;
import common.Command.CommandType;

import parser.GeneralParser;

public class ParserTest {

    @Test
    public void parseCommand_AddWithDescriptionOnly_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "a task with no time");
        Command actual = parser.parseCommand("add a task with no time");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void parseCommand_NoTime_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(0, 0)),
                null, new ArrayList<String>());
        Command actual = parser.parseCommand("add eat start 20-10-2010");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void parseCommand_AddWithEnd_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.of(2010, 10, 20), LocalTime.of(22, 30)), null);
        Command actual = parser.parseCommand("add eat start 22:30 20-10-2010");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void parseCommand_AddWithStartAndEnd_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "eat more food to get fat",
                LocalDateTime.of(LocalDate.of(2000, 10, 20), LocalTime.of(22, 30)),
                LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)));
        Command actual = parser.parseCommand("add eat more food to get fat start 22:30 20-10-2000 end 9:10 29-8-2004");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

//    @Test
    public void parseCommand_AddWithStartKeyWordInDescription_AddCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.ADD, "a task that starts at night");
        Command actual = parser.parseCommand("add a task that starts at night");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

//    @Test
    public void parseCommand_SaveNoDirectory_SaveCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.SAVE);
        Command actual = parser.parseCommand("save");
        assertEquals(expected.getType(), actual.getType());
    }

    @Test
    public void parse_SimpleAdd_DescriptionParser(){

    }
}
