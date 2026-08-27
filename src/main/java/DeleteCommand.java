/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private static final String COMMAND_NAME = "delete";

    private final int taskNumber;

    /**
     * Creates a command that deletes the specified task number.
     *
     * @param taskNumber the one-based task number to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        int taskIndex = getTaskIndex(taskNumber, COMMAND_NAME, tasks);
        Task deletedTask = tasks.delete(taskIndex);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
