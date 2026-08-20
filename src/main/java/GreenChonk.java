import java.util.Scanner;

/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final int BANNER_WIDTH = 61;
    private static final long FRAME_DELAY_MILLIS = 100;
    private static final int MAX_TASKS = 100;
    private static final String DIVIDER = "_".repeat(BANNER_WIDTH);
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";

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

                if (trimmedCommand.equalsIgnoreCase("list")) {
                    printTasks(tasks, taskCount);
                    continue;
                }

                if (trimmedCommand.startsWith("mark ")) {
                    int taskIndex = Integer.parseInt(trimmedCommand.substring("mark ".length()).trim()) - 1;
                    tasks[taskIndex].markAsDone();
                    System.out.println("Nice! Green Chonk marked this task as done:");
                    System.out.println("  " + tasks[taskIndex]);
                    continue;
                }

                if (trimmedCommand.startsWith("unmark ")) {
                    int taskIndex = Integer.parseInt(trimmedCommand.substring("unmark ".length()).trim()) - 1;
                    tasks[taskIndex].markAsNotDone();
                    System.out.println("OK, Green Chonk marked this task as not done yet:");
                    System.out.println("  " + tasks[taskIndex]);
                    continue;
                }

                if (trimmedCommand.startsWith(TODO_COMMAND)) {
                    String description = trimmedCommand.substring(TODO_COMMAND.length());
                    taskCount = addTask(tasks, taskCount, new Todo(description));
                    continue;
                }

                if (trimmedCommand.startsWith(DEADLINE_COMMAND)) {
                    String details = trimmedCommand.substring(DEADLINE_COMMAND.length());
                    String[] deadlineParts = details.split(DEADLINE_SEPARATOR, 2);
                    taskCount = addTask(tasks, taskCount, new Deadline(deadlineParts[0], deadlineParts[1]));
                    continue;
                }

                if (trimmedCommand.startsWith(EVENT_COMMAND)) {
                    String details = trimmedCommand.substring(EVENT_COMMAND.length());
                    String[] eventParts = details.split(EVENT_FROM_SEPARATOR, 2);
                    String[] timeParts = eventParts[1].split(EVENT_TO_SEPARATOR, 2);
                    taskCount = addTask(tasks, taskCount, new Event(eventParts[0], timeParts[0], timeParts[1]));
                    continue;
                }
            }
        }
    }

    /**
     * Stores a task and displays the updated task count.
     *
     * @param tasks the task storage array
     * @param taskCount the number of tasks currently stored
     * @param task the task to add
     * @return the updated number of stored tasks
     */
    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (taskCount >= MAX_TASKS) {
            return taskCount;
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
