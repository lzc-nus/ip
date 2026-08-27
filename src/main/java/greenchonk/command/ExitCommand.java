package greenchonk.command;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Ends the current Green Chonk session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current session.
     */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
