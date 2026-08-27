import java.time.LocalDate;

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
        TaskList tasks = loadTasks();

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
    private void executeCommand(String command, TaskList tasks) throws GreenChonkException {
        if (command.isEmpty()) {
            throw new GreenChonkException("Please enter a command. Try: todo buy milk");
        }

        if (command.equalsIgnoreCase("list")) {
            printTasks(tasks);
            return;
        }

        if (Parser.isCommand(command, SCHEDULE_COMMAND)) {
            printSchedule(command, tasks);
            return;
        }

        if (Parser.isCommand(command, MARK_COMMAND)) {
            updateTaskStatus(command, MARK_COMMAND, tasks, TaskStatus.DONE);
            return;
        }

        if (Parser.isCommand(command, UNMARK_COMMAND)) {
            updateTaskStatus(command, UNMARK_COMMAND, tasks, TaskStatus.NOT_DONE);
            return;
        }

        if (Parser.isCommand(command, DELETE_COMMAND)) {
            deleteTask(command, tasks);
            return;
        }

        if (Parser.isCommand(command, TODO_COMMAND)) {
            addTask(tasks, Parser.parseTodo(command));
            return;
        }

        if (Parser.isCommand(command, DEADLINE_COMMAND)) {
            addTask(tasks, Parser.parseDeadline(command));
            return;
        }

        if (Parser.isCommand(command, EVENT_COMMAND)) {
            addTask(tasks, Parser.parseEvent(command));
            return;
        }

        throw new GreenChonkException("I don't recognize \"" + command
                + "\". Try todo, deadline, event, list, schedule, mark, unmark, delete, or bye.");
    }

    /**
     * Displays deadlines and events that occur on a requested date.
     *
     * @param command the complete schedule command
     * @param tasks the tasks currently stored
     * @throws GreenChonkException if the schedule date is missing or invalid
     */
    private void printSchedule(String command, TaskList tasks) throws GreenChonkException {
        LocalDate date = Parser.parseScheduleDate(command);

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
    private void updateTaskStatus(String command, String commandName, TaskList tasks,
            TaskStatus newStatus) throws GreenChonkException {
        int taskIndex = Parser.parseTaskIndex(command, commandName, tasks.size());
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
    private void deleteTask(String command, TaskList tasks) throws GreenChonkException {
        int taskIndex = Parser.parseTaskIndex(command, DELETE_COMMAND, tasks.size());
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    /**
     * Stores a task and displays the updated task count.
     *
     * @param tasks the tasks currently stored
     * @param task the task to add
     * @throws GreenChonkException if the updated task list cannot be saved
     */
    private void addTask(TaskList tasks, Task task) throws GreenChonkException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk, or an empty task list if loading fails
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (GreenChonkException exception) {
            ui.showLoadingError(exception.getMessage());
            return new TaskList();
        }
    }

    /**
     * Displays all tasks currently stored in memory.
     *
     * @param tasks the tasks currently stored
     */
    private void printTasks(TaskList tasks) {
        ui.showTaskList(tasks);
    }
}
