package greenchonk.command;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.TaskStatus;
import greenchonk.ui.Ui;

/**
 * Marks a task as done or not done.
 */
public class UpdateStatusCommand extends Command {
    private final String commandName;
    private final TaskStatus newStatus;
    private final int taskNumber;

    /**
     * Creates a command that changes a task's completion status.
     *
     * @param taskNumber the one-based task number to update.
     * @param newStatus the completion status to apply.
     * @param commandName the command word used for validation feedback.
     */
    public UpdateStatusCommand(int taskNumber, TaskStatus newStatus,
            String commandName) {
        this.taskNumber = taskNumber;
        this.newStatus = newStatus;
        this.commandName = commandName;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        int taskIndex = getTaskIndex(taskNumber, commandName, tasks);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        setStatus(task, newStatus);

        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            setStatus(task, wasDone ? TaskStatus.DONE : TaskStatus.NOT_DONE);
            throw exception;
        }
        ui.showTaskStatusUpdated(task, newStatus);
    }

    private static void setStatus(Task task, TaskStatus status) {
        if (status == TaskStatus.DONE) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
}
