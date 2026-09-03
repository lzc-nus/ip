package greenchonk.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.StorageException;
import greenchonk.task.Task;
import greenchonk.task.TaskList;

/**
 * Loads tasks from a data file and saves task-list changes to that file.
 */
public class Storage {
    private final Path dataFile;
    private final TaskCodec taskCodec;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the path of the task data file.
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
        taskCodec = new TaskCodec();
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk.
     * @throws StorageException if the data file cannot be read or contains invalid data.
     */
    public List<Task> load() throws StorageException {
        List<Task> tasks = new ArrayList<>();
        try {
            Files.createDirectories(dataFile.getParent());
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }

            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
                String line = lines.get(lineNumber - 1);
                if (!line.isBlank()) {
                    tasks.add(taskCodec.decode(line, lineNumber));
                }
            }
        } catch (IOException exception) {
            throw new StorageException(exception.getMessage(), exception);
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks the tasks to persist.
     * @throws StorageException if the tasks cannot be encoded or written.
     */
    public void save(TaskList tasks) throws StorageException {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            lines.add(taskCodec.encode(tasks.get(index)));
        }

        try {
            Files.createDirectories(dataFile.getParent());
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new StorageException("I couldn't save the task list: " + exception.getMessage(),
                    exception);
        }
    }
}
