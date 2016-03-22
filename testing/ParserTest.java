import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import common.Command;
import common.Command.CommandType;

import parser.Parser;

public class ParserTest {
    @Test
    public void parseTest() {
        Parser parser = new Parser();
        Command expected = new Command(CommandType.ADD, "eat", LocalDateTime.of(LocalDate.of(2000, 10, 20), LocalTime.of(22, 30)), null);
        Command actual = parser.parseCommand("add eat start 22:30 20-10-2000");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }

    @Test
    public void moreTest() {
        Parser parser = new Parser();
        Command expected = new Command(CommandType.ADD, "eat more food to get fat",
                LocalDateTime.of(LocalDate.of(2000, 10, 20), LocalTime.of(22, 30)),
                LocalDateTime.of(LocalDate.of(2004, 8, 29), LocalTime.of(9, 10)));
        Command actual = parser.parseCommand("add eat more food to get fat start 22:30 20-10-2000 end 9:10 29-8-2004");
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getEndDate(), actual.getEndDate());
    }
}
