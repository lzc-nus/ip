package greenchonk.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import greenchonk.command.AddCommand;
import greenchonk.command.Command;
import greenchonk.command.DeleteCommand;
import greenchonk.command.ExitCommand;
import greenchonk.command.FindCommand;
import greenchonk.command.HelpCommand;
import greenchonk.command.ListCommand;
import greenchonk.command.ScheduleCommand;
import greenchonk.command.UpdateStatusCommand;
import greenchonk.exception.GreenChonkException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.TaskStatus;
import greenchonk.task.Todo;

/**
 * Interprets user commands and validates their arguments.
 */
public final class Parser {
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DELETE_COMMAND = "delete";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_COMMAND = "event";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String SCHEDULE_COMMAND = "schedule";
    private static final String TODO_COMMAND = "todo";
    private static final String UNMARK_COMMAND = "unmark";

    /**
     * Prevents instantiation of this command-parsing utility class.
     */
    private Parser() {
    }

    /**
     * Creates the command represented by the user's input.
     *
     * @param input the trimmed user input.
     * @return the parsed command.
     * @throws GreenChonkException if the command or its arguments are invalid.
     */
    public static Command parse(String input) throws GreenChonkException {
        if (input.isEmpty()) {
            throw new GreenChonkException("Please enter a command. Try: todo buy milk");
        }
        if (input.equalsIgnoreCase("bye")) {
            return new ExitCommand();
        }
        if (input.equalsIgnoreCase("list")) {
            return new ListCommand();
        }
        if (input.equalsIgnoreCase("help")) {
            return new HelpCommand();
        }
        if (isCommand(input, FIND_COMMAND)) {
            return new FindCommand(parseFindKeyword(input));
        }
        if (isCommand(input, SCHEDULE_COMMAND)) {
            return new ScheduleCommand(parseScheduleDate(input));
        }
        if (isCommand(input, MARK_COMMAND)) {
            return new UpdateStatusCommand(parseTaskNumber(input, MARK_COMMAND),
                    TaskStatus.DONE, MARK_COMMAND);
        }
        if (isCommand(input, UNMARK_COMMAND)) {
            return new UpdateStatusCommand(parseTaskNumber(input, UNMARK_COMMAND),
                    TaskStatus.NOT_DONE, UNMARK_COMMAND);
        }
        if (isCommand(input, DELETE_COMMAND)) {
            return new DeleteCommand(parseTaskNumber(input, DELETE_COMMAND));
        }
        if (isCommand(input, TODO_COMMAND)) {
            return new AddCommand(parseTodo(input));
        }
        if (isCommand(input, DEADLINE_COMMAND)) {
            return new AddCommand(parseDeadline(input));
        }
        if (isCommand(input, EVENT_COMMAND)) {
            return new AddCommand(parseEvent(input));
        }

        throw new GreenChonkException("I don't recognize \"" + input
                + "\". Try todo, deadline, event, list, find, schedule, mark, unmark, delete, help, or bye.");
    }

    /**
     * Returns the keyword supplied to a find command.
     *
     * @param command the complete find command.
     * @return the keyword to search for.
     * @throws GreenChonkException if the keyword is missing.
     */
    private static String parseFindKeyword(String command) throws GreenChonkException {
        String keyword = getArguments(command);
        if (keyword.isEmpty()) {
            throw new GreenChonkException("A find command needs a keyword. Try: find book");
        }
        return keyword;
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command the complete todo command.
     * @return the parsed todo.
     * @throws GreenChonkException if the description is empty.
     */
    private static Todo parseTodo(String command) throws GreenChonkException {
        String description = getArguments(command);
        if (description.isEmpty()) {
            throw new GreenChonkException("A todo needs a description. Try: todo buy milk");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and due date.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline.
     * @throws GreenChonkException if a required deadline field is missing or invalid.
     */
    private static Deadline parseDeadline(String command) throws GreenChonkException {
        String details = getArguments(command);
        int separatorPosition = details.indexOf(DEADLINE_SEPARATOR);
        if (separatorPosition < 0) {
            throw new GreenChonkException("A deadline needs /by followed by a date. "
                    + "Try: deadline submit report /by 2026-08-28");
        }

        String description = details.substring(0, separatorPosition).trim();
        String byText = details.substring(separatorPosition + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new GreenChonkException("A deadline needs a description before /by.");
        }
        if (byText.isEmpty()) {
            throw new GreenChonkException("A deadline needs a date after /by.");
        }
        LocalDate by = parseDate(byText, "deadline", "2026-08-28");
        return new Deadline(description, by);
    }

    /**
     * Creates an event after validating its description and date range.
     *
     * @param command the complete event command.
     * @return the parsed event.
     * @throws GreenChonkException if a required event field is missing or invalid.
     */
    private static Event parseEvent(String command) throws GreenChonkException {
        String details = getArguments(command);
        int fromPosition = details.indexOf(EVENT_FROM_SEPARATOR);
        if (fromPosition < 0) {
            throw new GreenChonkException("An event needs /from and /to. "
                    + "Try: event meeting /from 2026-08-28 /to 2026-08-29");
        }

        String description = details.substring(0, fromPosition).trim();
        String timeRange = details.substring(fromPosition + EVENT_FROM_SEPARATOR.length()).trim();
        int toPosition = timeRange.indexOf(EVENT_TO_SEPARATOR);
        if (description.isEmpty()) {
            throw new GreenChonkException("An event needs a description before /from.");
        }
        if (toPosition < 0) {
            throw new GreenChonkException("An event needs /to followed by an ending date.");
        }

        String fromText = timeRange.substring(0, toPosition).trim();
        String toText = timeRange.substring(toPosition + EVENT_TO_SEPARATOR.length()).trim();
        if (fromText.isEmpty()) {
            throw new GreenChonkException("An event needs a starting date after /from.");
        }
        if (toText.isEmpty()) {
            throw new GreenChonkException("An event needs an ending date after /to.");
        }
        LocalDate from = parseDate(fromText, "event start", "2026-08-28");
        LocalDate to = parseDate(toText, "event end", "2026-08-29");
        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException exception) {
            throw new GreenChonkException("An event's end date cannot be before its start date. "
                    + "Try /to " + from + " or later.");
        }
    }

    /**
     * Parses the requested date from a schedule command.
     *
     * @param command the complete schedule command.
     * @return the requested schedule date.
     * @throws GreenChonkException if the schedule date is missing or invalid.
     */
    private static LocalDate parseScheduleDate(String command) throws GreenChonkException {
        String dateText = getArguments(command);
        if (dateText.isEmpty()) {
            throw new GreenChonkException("Please provide a schedule date. Try: schedule 2026-08-28");
        }
        return parseDate(dateText, "schedule", "2026-08-28");
    }

    /**
     * Parses the one-based task number supplied to a mutation command.
     *
     * @param command the complete task mutation command
     * @param commandName the command word used in validation feedback
     * @return the parsed task number
     * @throws GreenChonkException if the task number is missing or not an integer
     */
    private static int parseTaskNumber(String command, String commandName)
            throws GreenChonkException {
        String numberText = getArguments(command);
        if (numberText.isEmpty()) {
            throw new GreenChonkException("Please provide a task number. Try: " + commandName + " 1");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new GreenChonkException("\"" + numberText
                    + "\" is not a valid task number. Use a whole number such as 1.");
        }

        return taskNumber;
    }

    /**
     * Returns whether the input contains the specified command word.
     * A command matches only when followed by the end of input or a space.
     *
     * @param input the complete user input
     * @param command the command word to match
     * @return true if the input starts with the complete command word
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Parses an ISO calendar date and translates parsing failures for the user.
     *
     * @param dateText the date text to parse
     * @param dateLabel the field name used in validation feedback
     * @param exampleDate a valid example used in validation feedback
     * @return the parsed date
     * @throws GreenChonkException if the date is invalid or not in ISO format
     */
    private static LocalDate parseDate(String dateText, String dateLabel, String exampleDate)
            throws GreenChonkException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new GreenChonkException("The " + dateLabel
                    + " date must use yyyy-MM-dd and be valid. Try: " + exampleDate);
        }
    }

    /**
     * Returns the trimmed text following the first command word.
     *
     * @param command the complete user command
     * @return the command arguments, or an empty string when none were supplied
     */
    private static String getArguments(String command) {
        int firstSpacePosition = command.indexOf(' ');
        if (firstSpacePosition < 0) {
            return "";
        }
        return command.substring(firstSpacePosition + 1).trim();
    }
}
