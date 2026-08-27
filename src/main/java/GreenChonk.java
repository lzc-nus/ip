import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final String DATA_FILE_PATH = "data/greenchonk.txt";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String SCHEDULE_COMMAND = "schedule";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates Green Chonk with a command-line UI and file-backed storage.
     *
     * @param filePath the path of the task data file
     */
    public GreenChonk(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
    }

    public static void main(String[] args) {
        new GreenChonk(DATA_FILE_PATH).run();
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        List<Task> tasks = loadTasks();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            String trimmedCommand = command.trim();
            if (trimmedCommand.equalsIgnoreCase("bye")) {
                ui.showGoodbye();
                return;
            }

            try {
                executeCommand(trimmedCommand, tasks);
            } catch (GreenChonkException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Executes one non-exit command.
     *
     * @param command the trimmed command entered by the user
     * @param tasks the tasks currently stored
     * @throws GreenChonkException if the command is invalid
     */
    private void executeCommand(String command, List<Task> tasks) throws GreenChonkException {
        if (command.isEmpty()) {
            throw new GreenChonkException("Please enter a command. Try: todo buy milk");
        }

        if (command.equalsIgnoreCase("list")) {
            printTasks(tasks);
            return;
        }

        if (isCommand(command, SCHEDULE_COMMAND)) {
            printSchedule(command, tasks);
            return;
        }

        if (isCommand(command, MARK_COMMAND)) {
            updateTaskStatus(command, MARK_COMMAND, tasks, TaskStatus.DONE);
            return;
        }

        if (isCommand(command, UNMARK_COMMAND)) {
            updateTaskStatus(command, UNMARK_COMMAND, tasks, TaskStatus.NOT_DONE);
            return;
        }

        if (isCommand(command, DELETE_COMMAND)) {
            deleteTask(command, tasks);
            return;
        }

        if (isCommand(command, TODO_COMMAND)) {
            addTask(tasks, parseTodo(command));
            return;
        }

        if (isCommand(command, DEADLINE_COMMAND)) {
            addTask(tasks, parseDeadline(command));
            return;
        }

        if (isCommand(command, EVENT_COMMAND)) {
            addTask(tasks, parseEvent(command));
            return;
        }

        throw new GreenChonkException("I don't recognize \"" + command
                + "\". Try todo, deadline, event, list, schedule, mark, unmark, delete, or bye.");
    }

    /**
     * Checks whether the input is a command word, optionally followed by arguments.
     *
     * @param input the trimmed user input
     * @param command the command word to match
     * @return true if the input invokes the command
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command the complete todo command
     * @return the parsed todo
     * @throws GreenChonkException if the description is empty
     */
    private static Todo parseTodo(String command) throws GreenChonkException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new GreenChonkException("A todo needs a description. Try: todo buy milk");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and due value.
     *
     * @param command the complete deadline command
     * @return the parsed deadline
     * @throws GreenChonkException if a required deadline field is missing
     */
    private static Deadline parseDeadline(String command) throws GreenChonkException {
        String details = command.substring(DEADLINE_COMMAND.length()).trim();
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
     * Creates an event after validating its description and time range.
     *
     * @param command the complete event command
     * @return the parsed event
     * @throws GreenChonkException if a required event field is missing
     */
    private static Event parseEvent(String command) throws GreenChonkException {
        String details = command.substring(EVENT_COMMAND.length()).trim();
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
     * Parses an ISO calendar date and converts invalid input into a user-facing error.
     *
     * @param dateText the date text to parse
     * @param dateLabel the field name to identify in an error
     * @param exampleDate an example of a valid date
     * @return the parsed date
     * @throws GreenChonkException if the date is invalid or has the wrong format
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
     * Displays deadlines and events that occur on a requested date.
     *
     * @param command the complete schedule command
     * @param tasks the tasks currently stored
     * @throws GreenChonkException if the schedule date is missing or invalid
     */
    private void printSchedule(String command, List<Task> tasks) throws GreenChonkException {
        String dateText = command.substring(SCHEDULE_COMMAND.length()).trim();
        if (dateText.isEmpty()) {
            throw new GreenChonkException("Please provide a schedule date. Try: schedule 2026-08-28");
        }
        LocalDate date = parseDate(dateText, "schedule", "2026-08-28");

        boolean hasScheduledTask = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.occursOn(date)) {
                if (!hasScheduledTask) {
                    ui.showScheduleHeader(date);
                }
                ui.showNumberedTask(index + 1, task);
                hasScheduledTask = true;
            }
        }
        if (!hasScheduledTask) {
            ui.showEmptySchedule(date);
        }
    }

    /**
     * Marks or unmarks a task after validating the requested task number.
     *
     * @param command the complete mark or unmark command
     * @param commandName the command word being executed
     * @param tasks the tasks currently stored
     * @param newStatus the completion status to apply
     * @throws GreenChonkException if the task number is missing, invalid, or outside the list
     */
    private void updateTaskStatus(String command, String commandName, List<Task> tasks,
            TaskStatus newStatus) throws GreenChonkException {
        int taskIndex = parseTaskIndex(command, commandName, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (newStatus == TaskStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }

        ui.showTaskStatusUpdated(task, newStatus);
    }

    /**
     * Deletes the requested task and displays the remaining task count.
     *
     * @param command the complete delete command
     * @param tasks the tasks currently stored
     * @throws GreenChonkException if the task number is missing, invalid, or outside the list
     */
    private void deleteTask(String command, List<Task> tasks) throws GreenChonkException {
        int taskIndex = parseTaskIndex(command, DELETE_COMMAND, tasks.size());
        Task deletedTask = tasks.remove(taskIndex);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Converts a user-facing task number into an array index.
     *
     * @param command the complete mark or unmark command
     * @param commandName the command word being executed
     * @param taskCount the number of tasks currently stored
     * @return the zero-based task index
     * @throws GreenChonkException if the task number cannot identify an existing task
     */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws GreenChonkException {
        String numberText = command.substring(commandName.length()).trim();
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
            throw new GreenChonkException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Stores a task and displays the updated task count.
     *
     * @param tasks the tasks currently stored
     * @param task the task to add
     * @throws GreenChonkException if the updated task list cannot be saved
     */
    private void addTask(List<Task> tasks, Task task) throws GreenChonkException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk, or an empty list if loading fails
     */
    private List<Task> loadTasks() {
        try {
            return storage.load();
        } catch (GreenChonkException exception) {
            ui.showLoadingError(exception.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Displays all tasks currently stored in memory.
     *
     * @param tasks the tasks currently stored
     */
    private void printTasks(List<Task> tasks) {
        ui.showTaskList(tasks);
    }
}
