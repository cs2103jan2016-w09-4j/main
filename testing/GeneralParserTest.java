import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import common.Command;
import common.Command.CommandType;

import parser.GeneralParser;
import parser.InvalidCommandException;
import parser.EmptyCommandException;
import parser.WrongCommandFormatException;

public class GeneralParserTest {

    @Test
    public void parseCommand_AddWithDescription() {
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
    public void parseCommand_AddCommandWithTime() {
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
    public void parseCommand_AddWithStartAndEndFullTimeFormat() {
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

    @Test(expected=InvalidCommandException.class)
    public void parseCommand_InvalidCommandType() throws InvalidCommandException {
        GeneralParser parser = new GeneralParser();
        try {
            Command actual = parser.parseCommand("clean");
            fail();
        }
        catch (WrongCommandFormatException|EmptyCommandException e) {
            fail();
        }
    }

    @Test(expected=WrongCommandFormatException.class)
    public void parseCommand_HashtagBeforeStartTime_ReturnWrongCommandFormatException() throws WrongCommandFormatException {
        GeneralParser parser = new GeneralParser();

        try {
            Command actual = parser.parseCommand("add work part-time #important start 4pm");
            fail();
        }
        catch (InvalidCommandException|EmptyCommandException e) {
            fail();
        }
    }

    @Test
    public void parseCommand_EditCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.EDIT, 4, "go jogging");

        try {
            Command actual = parser.parseCommand("edit 4 go jogging");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getId(), actual.getId());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void parseCommand_SearchCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.SEARCH, "jogging");

        try {
            Command actual = parser.parseCommand("search jogging");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_SearchdoneCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.SEARCHDONE, "food");

        try {
            Command actual = parser.parseCommand("searchdone food");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_DeleteCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.DELETE, 5);

        try {
            Command actual = parser.parseCommand("delete 5");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getId(), actual.getId());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_DoneCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.DONE, 5);

        try {
            Command actual = parser.parseCommand("done 5");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getId(), actual.getId());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_SaveCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.SAVE, "C:\\GridTask");

        try {
            Command actual = parser.parseCommand("save C:\\GridTask");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_LoadCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.LOAD, "C:\\demo save.txt");

        try {
            Command actual = parser.parseCommand("load C:\\demo save.txt");
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getId(), actual.getId());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_UndoCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.UNDO);

        try {
            Command actual = parser.parseCommand("undo");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_RedoCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.REDO);

        try {
            Command actual = parser.parseCommand("redo");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_HomeCommand_HasRedundantCharacter() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.HOME);

        try {
            Command actual = parser.parseCommand("home +redundant character");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_HelpCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.HELP);

        try {
            Command actual = parser.parseCommand("help");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void parseCommand_ExitCommand() {
        GeneralParser parser = new GeneralParser();
        Command expected = new Command(CommandType.EXIT);

        try {
            Command actual = parser.parseCommand("exit");
            assertEquals(expected.getType(), actual.getType());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
