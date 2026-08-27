import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets user commands and validates their arguments.
 */
public final class Parser {
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";

    private Parser() {
    }

    /**
     * Returns whether the input is a command word, optionally followed by arguments.
     *
     * @param input the trimmed user input
     * @param command the command word to match
     * @return true if the input invokes the command
     */
    public static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command the complete todo command
     * @return the parsed todo
     * @throws GreenChonkException if the description is empty
     */
    public static Todo parseTodo(String command) throws GreenChonkException {
        String description = getArguments(command);
        if (description.isEmpty()) {
            throw new GreenChonkException("A todo needs a description. Try: todo buy milk");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and due date.
     *
     * @param command the complete deadline command
     * @return the parsed deadline
     * @throws GreenChonkException if a required deadline field is missing or invalid
     */
    public static Deadline parseDeadline(String command) throws GreenChonkException {
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
     * @param command the complete event command
     * @return the parsed event
     * @throws GreenChonkException if a required event field is missing or invalid
     */
    public static Event parseEvent(String command) throws GreenChonkException {
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
     * @param command the complete schedule command
     * @return the requested schedule date
     * @throws GreenChonkException if the schedule date is missing or invalid
     */
    public static LocalDate parseScheduleDate(String command) throws GreenChonkException {
        String dateText = getArguments(command);
        if (dateText.isEmpty()) {
            throw new GreenChonkException("Please provide a schedule date. Try: schedule 2026-08-28");
        }
        return parseDate(dateText, "schedule", "2026-08-28");
    }

    /**
     * Converts a user-facing task number into a zero-based index.
     *
     * @param command the complete command containing the task number
     * @param commandName the command word being executed
     * @param taskCount the number of tasks currently stored
     * @return the zero-based task index
     * @throws GreenChonkException if the task number cannot identify an existing task
     */
    public static int parseTaskIndex(String command, String commandName, int taskCount)
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

        if (taskCount == 0) {
            throw new GreenChonkException("There are no tasks to " + commandName + " yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new GreenChonkException("Task " + taskNumber
                    + " does not exist. Choose a number from 1 to " + taskCount + ".");
        }
        return taskNumber - 1;
    }

    private static LocalDate parseDate(String dateText, String dateLabel, String exampleDate)
            throws GreenChonkException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new GreenChonkException("The " + dateLabel
                    + " date must use yyyy-MM-dd and be valid. Try: " + exampleDate);
        }
    }

    private static String getArguments(String command) {
        int firstSpacePosition = command.indexOf(' ');
        if (firstSpacePosition < 0) {
            return "";
        }
        return command.substring(firstSpacePosition + 1).trim();
    }
}
