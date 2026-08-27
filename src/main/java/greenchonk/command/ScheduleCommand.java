package greenchonk.command;

import java.time.LocalDate;

import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Displays deadlines and events that occur on a requested date.
 */
public class ScheduleCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that displays tasks occurring on the specified date.
     *
     * @param date the date whose scheduled tasks should be displayed
     */
    public ScheduleCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines and events occurring on the configured date.
     * Preserves each matching task's position in the complete task list.
     *
     * @param tasks the task list to search
     * @param ui the UI that displays matching tasks
     * @param storage the unused task storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
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
}
