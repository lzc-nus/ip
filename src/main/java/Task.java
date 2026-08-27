/**
 * Represents a task and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private TaskStatus status;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    protected Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when completed, or a space otherwise
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is completed
     */
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to identify this task's type.
     *
     * @return the task type symbol
     */
    protected abstract String getTypeIcon();

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
