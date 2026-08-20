import java.util.Scanner;

/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final int BANNER_WIDTH = 61;
    private static final long FRAME_DELAY_MILLIS = 100;
    private static final int MAX_TASKS = 100;
    private static final String DIVIDER = "_".repeat(BANNER_WIDTH);
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";

    private static final String BANNER = """
                  ____                       ____ _                 _
                 / ___|_ __ ___  ___ _ __   / ___| |__   ___  _ __ | | __
                | |  _| '__/ _ \\/ _ \\ '_ \\ | |   | '_ \\ / _ \\| '_ \\| |/ /
                | |_| | | |  __/  __/ | | || |___| | | | (_) | | | |   <
                 \\____|_|  \\___|\\___|_| |_| \\____|_| |_|\\___/|_| |_|\\_|\\_\\
                """;

    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println();
        animateMessage("Green Chonk is waking up...");
        animateMessage("Hello! I'm Green Chonk.");
        animateMessage("Ready to chomp through your tasks!");
        animateMessage("What can I do for you?");
        System.out.println();
        System.out.println(DIVIDER);

        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                String trimmedCommand = command.trim();
                if (trimmedCommand.equalsIgnoreCase("bye")) {
                    System.out.println();
                    System.out.println(DIVIDER);
                    animateMessage("Bye! I'm rolling off for now. See you again soon!");
                    System.out.println(DIVIDER);
                    return;
                }

                try {
                    taskCount = executeCommand(trimmedCommand, tasks, taskCount);
                } catch (GreenChonkException exception) {
                    printError(exception);
                }
            }
        }
    }

    /**
     * Executes one non-exit command.
     *
     * @param command the trimmed command entered by the user
     * @param tasks the task storage array
     * @param taskCount the number of tasks currently stored
     * @return the updated number of stored tasks
     * @throws GreenChonkException if the command is invalid
     */
    private static int executeCommand(String command, Task[] tasks, int taskCount)
            throws GreenChonkException {
        if (command.isEmpty()) {
            throw new GreenChonkException("Please enter a command. Try: todo buy milk");
        }

        if (command.equalsIgnoreCase("list")) {
            printTasks(tasks, taskCount);
            return taskCount;
        }

        if (isCommand(command, MARK_COMMAND)) {
            updateTaskStatus(command, MARK_COMMAND, tasks, taskCount, true);
            return taskCount;
        }

        if (isCommand(command, UNMARK_COMMAND)) {
            updateTaskStatus(command, UNMARK_COMMAND, tasks, taskCount, false);
            return taskCount;
        }

        if (isCommand(command, TODO_COMMAND)) {
            return addTask(tasks, taskCount, parseTodo(command));
        }

        if (isCommand(command, DEADLINE_COMMAND)) {
            return addTask(tasks, taskCount, parseDeadline(command));
        }

        if (isCommand(command, EVENT_COMMAND)) {
            return addTask(tasks, taskCount, parseEvent(command));
        }

        throw new GreenChonkException("I don't recognize \"" + command
                + "\". Try todo, deadline, event, list, mark, unmark, or bye.");
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
            throw new GreenChonkException("A deadline needs /by followed by a date or time. "
                    + "Try: deadline submit report /by Friday 5pm");
        }

        String description = details.substring(0, separatorPosition).trim();
        String by = details.substring(separatorPosition + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new GreenChonkException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new GreenChonkException("A deadline needs a date or time after /by.");
        }
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
                    + "Try: event meeting /from Monday 2pm /to 4pm");
        }

        String description = details.substring(0, fromPosition).trim();
        String timeRange = details.substring(fromPosition + EVENT_FROM_SEPARATOR.length()).trim();
        int toPosition = timeRange.indexOf(EVENT_TO_SEPARATOR);
        if (description.isEmpty()) {
            throw new GreenChonkException("An event needs a description before /from.");
        }
        if (toPosition < 0) {
            throw new GreenChonkException("An event needs /to followed by an ending date or time.");
        }

        String from = timeRange.substring(0, toPosition).trim();
        String to = timeRange.substring(toPosition + EVENT_TO_SEPARATOR.length()).trim();
        if (from.isEmpty()) {
            throw new GreenChonkException("An event needs a starting date or time after /from.");
        }
        if (to.isEmpty()) {
            throw new GreenChonkException("An event needs an ending date or time after /to.");
        }
        return new Event(description, from, to);
    }

    /**
     * Marks or unmarks a task after validating the requested task number.
     *
     * @param command the complete mark or unmark command
     * @param commandName the command word being executed
     * @param tasks the task storage array
     * @param taskCount the number of tasks currently stored
     * @param isDone true to mark the task as done, or false to mark it as not done
     * @throws GreenChonkException if the task number is missing, invalid, or outside the list
     */
    private static void updateTaskStatus(String command, String commandName, Task[] tasks,
            int taskCount, boolean isDone) throws GreenChonkException {
        int taskIndex = parseTaskIndex(command, commandName, taskCount);
        if (isDone) {
            tasks[taskIndex].markAsDone();
            System.out.println("Nice! Green Chonk marked this task as done:");
        } else {
            tasks[taskIndex].markAsNotDone();
            System.out.println("OK, Green Chonk marked this task as not done yet:");
        }
        System.out.println("  " + tasks[taskIndex]);
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
     * @param tasks the task storage array
     * @param taskCount the number of tasks currently stored
     * @param task the task to add
     * @return the updated number of stored tasks
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) throws GreenChonkException {
        if (taskCount >= MAX_TASKS) {
            throw new GreenChonkException("Green Chonk cannot carry more than " + MAX_TASKS + " tasks.");
        }

        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        String taskLabel = updatedTaskCount == 1 ? "task" : "tasks";
        System.out.println("Chomped this task:");
        System.out.println("  " + task);
        System.out.println("Green Chonk is now carrying " + updatedTaskCount + " " + taskLabel + ".");
        return updatedTaskCount;
    }

    /**
     * Displays a user-facing explanation for an invalid command.
     *
     * @param exception the command error to display
     */
    private static void printError(GreenChonkException exception) {
        System.out.println("Oops! Green Chonk couldn't chomp that:");
        System.out.println("  " + exception.getMessage());
    }

    /**
     * Displays all tasks currently stored in memory.
     *
     * @param tasks the task storage array
     * @param taskCount the number of tasks currently stored
     */
    private static void printTasks(Task[] tasks, int taskCount) {
        if (taskCount == 0) {
            System.out.println("Green Chonk is not carrying any tasks yet.");
            return;
        }

        System.out.println("Here are the tasks Green Chonk is carrying:");
        for (int index = 0; index < taskCount; index++) {
            System.out.println((index + 1) + "." + tasks[index]);
        }
    }

    /**
     * Prints a message after a short, centered thinking animation.
     *
     * @param message the message to reveal after the animation
     */
    private static void animateMessage(String message) {
        for (int dotCount = 1; dotCount <= 3; dotCount++) {
            System.out.print("\r" + center(".".repeat(dotCount)));
            System.out.flush();
            try {
                Thread.sleep(FRAME_DELAY_MILLIS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.print("\r" + center(message));
        System.out.println();
    }

    /**
     * Centers text using the width of the ASCII banner.
     *
     * @param text the text to center
     * @return the text with leading spaces added
     */
    private static String center(String text) {
        int leftPadding = Math.max(0, (BANNER_WIDTH - text.length()) / 2);
        return " ".repeat(leftPadding) + text;
    }
}
