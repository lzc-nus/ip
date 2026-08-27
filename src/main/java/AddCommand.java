/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
