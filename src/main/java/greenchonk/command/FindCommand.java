package greenchonk.command;

import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the specified keyword.
     *
     * @param keyword the keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        boolean hasMatchingTask = false;
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            if (task.matches(keyword)) {
                if (!hasMatchingTask) {
                    ui.showFindHeader();
                }
                ui.showNumberedTask(index + 1, task);
                hasMatchingTask = true;
            }
        }
        if (!hasMatchingTask) {
            ui.showNoMatchingTasks();
        }
    }
}
