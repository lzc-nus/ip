package greenchonk.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.TaskStatus;
import greenchonk.ui.Ui;

/**
 * Provides observable collaborators for isolated command tests.
 */
final class CommandTestSupport {
    private CommandTestSupport() {
    }

    static final class RecordingStorage extends Storage {
        private boolean shouldFail;
        private int saveCount;

        RecordingStorage() {
            super("unused/data.txt");
        }

        void failNextSave() {
            shouldFail = true;
        }

        int getSaveCount() {
            return saveCount;
        }

        @Override
        public void save(TaskList tasks) throws GreenChonkException {
            saveCount++;
            if (shouldFail) {
                throw new GreenChonkException("save failed");
            }
        }
    }

    static final class RecordingUi extends Ui {
        Task addedTask;
        Task deletedTask;
        Task updatedTask;
        TaskStatus updatedStatus;
        int taskCount;
        int remainingTaskCount;
        boolean goodbyeShown;
        boolean findHeaderShown;
        boolean noMatchingTasksShown;
        TaskList listedTasks;
        LocalDate scheduleDate;
        LocalDate emptyScheduleDate;
        final List<Integer> shownTaskNumbers = new ArrayList<>();
        final List<Task> shownTasks = new ArrayList<>();

        @Override
        public void showGoodbye() {
            goodbyeShown = true;
        }

        @Override
        public void showScheduleHeader(LocalDate date) {
            scheduleDate = date;
        }

        @Override
        public void showNumberedTask(int taskNumber, Task task) {
            shownTaskNumbers.add(taskNumber);
            shownTasks.add(task);
        }

        @Override
        public void showEmptySchedule(LocalDate date) {
            emptyScheduleDate = date;
        }

        @Override
        public void showTaskStatusUpdated(Task task, TaskStatus status) {
            updatedTask = task;
            updatedStatus = status;
        }

        @Override
        public void showTaskDeleted(Task task, int remainingTaskCount) {
            deletedTask = task;
            this.remainingTaskCount = remainingTaskCount;
        }

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            addedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showTaskList(TaskList tasks) {
            listedTasks = tasks;
        }

        @Override
        public void showFindHeader() {
            findHeaderShown = true;
        }

        @Override
        public void showNoMatchingTasks() {
            noMatchingTasksShown = true;
        }
    }
}
