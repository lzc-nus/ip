package greenchonk.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import greenchonk.command.AddCommand;
import greenchonk.command.DeleteCommand;
import greenchonk.command.ExitCommand;
import greenchonk.command.FindCommand;
import greenchonk.command.ListCommand;
import greenchonk.command.ScheduleCommand;
import greenchonk.command.UpdateStatusCommand;
import greenchonk.exception.GreenChonkException;

class ParserTest {
    @Test
    void parse_exitAndListIgnoringCase_correctCommandCreated() throws GreenChonkException {
        assertInstanceOf(ExitCommand.class, Parser.parse("BYE"));
        assertInstanceOf(ListCommand.class, Parser.parse("LiSt"));
    }

    @Test
    void parse_validTaskCreationCommands_addCommandCreated() throws GreenChonkException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("deadline submit report /by 2026-08-28"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event conference /from 2026-08-28 /to 2026-08-30"));
    }

    @Test
    void parse_validTaskMutationCommands_correctCommandCreated() throws GreenChonkException {
        assertInstanceOf(UpdateStatusCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UpdateStatusCommand.class, Parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    void parse_validScheduleCommand_scheduleCommandCreated() throws GreenChonkException {
        assertInstanceOf(ScheduleCommand.class, Parser.parse("schedule 2026-08-28"));
    }

    @Test
    void parse_validFindCommand_findCommandCreated() throws GreenChonkException {
        assertInstanceOf(FindCommand.class, Parser.parse("find read book"));
    }

    @Test
    void parse_blankCommand_exceptionThrown() {
        assertParseError("", "Please enter a command. Try: todo buy milk");
    }

    @Test
    void parse_unknownOrExtendedSimpleCommand_exceptionThrown() {
        assertParseError("roll away",
                "I don't recognize \"roll away\". Try todo, deadline, event, list, "
                        + "find, schedule, mark, unmark, delete, or bye.");
        assertParseError("bye now",
                "I don't recognize \"bye now\". Try todo, deadline, event, list, "
                        + "find, schedule, mark, unmark, delete, or bye.");
        assertParseError("listing",
                "I don't recognize \"listing\". Try todo, deadline, event, list, "
                        + "find, schedule, mark, unmark, delete, or bye.");
    }

    @Test
    void parse_todoWithoutDescription_exceptionThrown() {
        assertParseError("todo", "A todo needs a description. Try: todo buy milk");
    }

    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        assertParseError("find", "A find command needs a keyword. Try: find book");
    }

    @Test
    void parse_deadlineWithMissingFields_exceptionThrown() {
        assertParseError("deadline submit report",
                "A deadline needs /by followed by a date. "
                        + "Try: deadline submit report /by 2026-08-28");
        assertParseError("deadline /by 2026-08-28",
                "A deadline needs a description before /by.");
        assertParseError("deadline submit report /by",
                "A deadline needs a date after /by.");
    }

    @Test
    void parse_deadlineWithInvalidDate_exceptionThrown() {
        assertParseError("deadline submit report /by 2026-02-29",
                "The deadline date must use yyyy-MM-dd and be valid. Try: 2026-08-28");
    }

    @Test
    void parse_eventWithMissingFields_exceptionThrown() {
        assertParseError("event meeting /to 2026-08-29",
                "An event needs /from and /to. "
                        + "Try: event meeting /from 2026-08-28 /to 2026-08-29");
        assertParseError("event /from 2026-08-28 /to 2026-08-29",
                "An event needs a description before /from.");
        assertParseError("event meeting /from 2026-08-28",
                "An event needs /to followed by an ending date.");
        assertParseError("event meeting /from /to 2026-08-29",
                "An event needs a starting date after /from.");
        assertParseError("event meeting /from 2026-08-28 /to",
                "An event needs an ending date after /to.");
    }

    @Test
    void parse_eventWithInvalidDates_exceptionThrown() {
        assertParseError("event meeting /from tomorrow /to 2026-08-29",
                "The event start date must use yyyy-MM-dd and be valid. Try: 2026-08-28");
        assertParseError("event meeting /from 2026-08-28 /to tomorrow",
                "The event end date must use yyyy-MM-dd and be valid. Try: 2026-08-29");
        assertParseError("event meeting /from 2026-08-30 /to 2026-08-29",
                "An event's end date cannot be before its start date. "
                        + "Try /to 2026-08-30 or later.");
    }

    @Test
    void parse_taskMutationWithMissingOrNonNumericNumber_exceptionThrown() {
        assertParseError("mark", "Please provide a task number. Try: mark 1");
        assertParseError("unmark two",
                "\"two\" is not a valid task number. Use a whole number such as 1.");
        assertParseError("delete 1 2",
                "\"1 2\" is not a valid task number. Use a whole number such as 1.");
    }

    @Test
    void parse_scheduleWithMissingOrInvalidDate_exceptionThrown() {
        assertParseError("schedule",
                "Please provide a schedule date. Try: schedule 2026-08-28");
        assertParseError("schedule next Friday",
                "The schedule date must use yyyy-MM-dd and be valid. Try: 2026-08-28");
    }

    private static void assertParseError(String input, String expectedMessage) {
        GreenChonkException exception = assertThrows(GreenChonkException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
