import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Handles command-line interaction between Green Chonk and the user.
 */
public class Ui {
    private static final int BANNER_WIDTH = 61;
    private static final long FRAME_DELAY_MILLIS = 100;
    private static final String DIVIDER = "_".repeat(BANNER_WIDTH);
    private static final String BANNER = """
                  ____                       ____ _                 _
                 / ___|_ __ ___  ___ _ __   / ___| |__   ___  _ __ | | __
                | |  _| '__/ _ \\/ _ \\ '_ \\ | |   | '_ \\ / _ \\| '_ \\| |/ /
                | |_| | | |  __/  __/ | | || |___| | | | (_) | | | |   <
                 \\____|_|  \\___|\\___|_| |_| \\____|_| |_|\\___/|_| |_|\\_|\\_\\
                """;

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays Green Chonk's banner and welcome messages.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println();
        animateMessage("Green Chonk is waking up...");
        animateMessage("Hello! I'm Green Chonk.");
        animateMessage("Ready to chomp through your tasks!");
        animateMessage("What can I do for you?");
        System.out.println();
        System.out.println(DIVIDER);
    }

    /**
     * Returns whether another command can be read.
     *
     * @return true if standard input contains another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the command as entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Green Chonk's farewell.
     */
    public void showGoodbye() {
        System.out.println();
        System.out.println(DIVIDER);
        animateMessage("Bye! I'm rolling off for now. See you again soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Displays a user-facing command error.
     *
     * @param message the explanation of the error
     */
    public void showError(String message) {
        System.out.println("Oops! Green Chonk couldn't chomp that:");
        System.out.println("  " + message);
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message the explanation of the loading failure
     */
    public void showLoadingError(String message) {
        System.out.println("Oops! Green Chonk couldn't load saved tasks:");
        System.out.println("  " + message);
    }

    /**
     * Displays the heading for tasks scheduled on a date.
     *
     * @param date the requested schedule date
     */
    public void showScheduleHeader(LocalDate date) {
        System.out.println("Here are the tasks scheduled for " + date + ":");
    }

    /**
     * Displays one task with its position in the full task list.
     *
     * @param taskNumber the task's one-based position
     * @param task the task to display
     */
    public void showNumberedTask(int taskNumber, Task task) {
        System.out.println(taskNumber + "." + task);
    }

    /**
     * Reports that no deadline or event occurs on the requested date.
     *
     * @param date the requested schedule date
     */
    public void showEmptySchedule(LocalDate date) {
        System.out.println("Green Chonk has no deadlines or events scheduled for " + date + ".");
    }

    /**
     * Displays a task whose completion status changed.
     *
     * @param task the updated task
     * @param status the task's new status
     */
    public void showTaskStatusUpdated(Task task, TaskStatus status) {
        if (status == TaskStatus.DONE) {
            System.out.println("Nice! Green Chonk marked this task as done:");
        } else {
            System.out.println("OK, Green Chonk marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    /**
     * Displays a deleted task and the number of remaining tasks.
     *
     * @param task the deleted task
     * @param remainingTaskCount the number of tasks remaining
     */
    public void showTaskDeleted(Task task, int remainingTaskCount) {
        System.out.println("Noted. Green Chonk removed this task:");
        System.out.println("  " + task);
        System.out.println("Green Chonk is now carrying " + taskCountText(remainingTaskCount) + ".");
    }

    /**
     * Displays an added task and the updated task count.
     *
     * @param task the added task
     * @param taskCount the updated number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Chomped this task:");
        System.out.println("  " + task);
        System.out.println("Green Chonk is now carrying " + taskCountText(taskCount) + ".");
    }

    /**
     * Displays all tasks in their current order.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Green Chonk is not carrying any tasks yet.");
            return;
        }

        System.out.println("Here are the tasks Green Chonk is carrying:");
        for (int index = 0; index < tasks.size(); index++) {
            showNumberedTask(index + 1, tasks.get(index));
        }
    }

    private static String taskCountText(int taskCount) {
        String taskLabel = taskCount == 1 ? "task" : "tasks";
        return taskCount + " " + taskLabel;
    }

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

    private static String center(String text) {
        int leftPadding = Math.max(0, (BANNER_WIDTH - text.length()) / 2);
        return " ".repeat(leftPadding) + text;
    }
}
