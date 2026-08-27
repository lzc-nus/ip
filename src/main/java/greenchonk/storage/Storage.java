package greenchonk.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.GreenChonkException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

/**
 * Loads tasks from a data file and saves task-list changes to that file.
 */
public class Storage {
    private static final String DATA_SEPARATOR = " | ";

    private final Path dataFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath the path of the task data file
     */
    public Storage(String filePath) {
        dataFile = Path.of(filePath);
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk
     * @throws GreenChonkException if the data file cannot be read or contains invalid data
     */
    public List<Task> load() throws GreenChonkException {
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
                    tasks.add(parseSavedTask(line, lineNumber));
                }
            }
        } catch (IOException exception) {
            throw new GreenChonkException(exception.getMessage());
        }
        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks the tasks to persist
     * @throws GreenChonkException if the tasks cannot be written
     */
    public void save(TaskList tasks) throws GreenChonkException {
        List<String> lines = new ArrayList<>();
        for (int index = 0; index < tasks.size(); index++) {
            lines.add(serializeTask(tasks.get(index)));
        }

        try {
            Files.createDirectories(dataFile.getParent());
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new GreenChonkException("I couldn't save the task list: " + exception.getMessage());
        }
    }

    /**
     * Converts one task to the human-readable storage format.
     *
     * @param task the task to serialize
     * @return one line suitable for the data file
     */
    private static String serializeTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        String commonFields = task.getTypeIcon() + DATA_SEPARATOR + status
                + DATA_SEPARATOR + escapeDataField(task.getDescription());
        if (task instanceof Deadline deadline) {
            return commonFields + DATA_SEPARATOR + deadline.getBy();
        }
        if (task instanceof Event event) {
            return commonFields + DATA_SEPARATOR + event.getFrom()
                    + DATA_SEPARATOR + event.getTo();
        }
        return commonFields;
    }

    /**
     * Recreates one task from a line in the data file.
     *
     * @param line the stored task line
     * @param lineNumber the line number used in error messages
     * @return the restored task
     * @throws GreenChonkException if the line does not use the expected format
     */
    private static Task parseSavedTask(String line, int lineNumber) throws GreenChonkException {
        List<String> fields = splitDataLine(line, lineNumber);
        if (fields.size() < 3) {
            throw invalidDataLine(lineNumber);
        }
        for (String field : fields) {
            if (field.isEmpty()) {
                throw invalidDataLine(lineNumber);
            }
        }

        String type = fields.get(0);
        String status = fields.get(1);
        Task task;
        switch (type) {
            case "T":
                if (fields.size() != 3) {
                    throw invalidDataLine(lineNumber);
                }
                task = new Todo(fields.get(2));
                break;
            case "D":
                if (fields.size() != 4) {
                    throw invalidDataLine(lineNumber);
                }
                task = new Deadline(fields.get(2), parseSavedDate(fields.get(3), lineNumber));
                break;
            case "E":
                if (fields.size() != 5) {
                    throw invalidDataLine(lineNumber);
                }
                try {
                    task = new Event(fields.get(2), parseSavedDate(fields.get(3), lineNumber),
                            parseSavedDate(fields.get(4), lineNumber));
                } catch (IllegalArgumentException exception) {
                    throw invalidDataLine(lineNumber);
                }
                break;
            default:
                throw invalidDataLine(lineNumber);
        }

        if (status.equals("1")) {
            task.markAsDone();
        } else if (!status.equals("0")) {
            throw invalidDataLine(lineNumber);
        }
        return task;
    }

    /**
     * Parses a canonical date stored in the data file.
     *
     * @param dateText the stored date text
     * @param lineNumber the line number used in error messages
     * @return the parsed date
     * @throws GreenChonkException if the stored date is invalid
     */
    private static LocalDate parseSavedDate(String dateText, int lineNumber)
            throws GreenChonkException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw invalidDataLine(lineNumber);
        }
    }

    /**
     * Splits a stored line while preserving escaped pipe and backslash characters.
     *
     * @param line the stored line to split
     * @param lineNumber the line number used in error messages
     * @return the unescaped fields
     * @throws GreenChonkException if the line ends with an incomplete escape
     */
    private static List<String> splitDataLine(String line, int lineNumber)
            throws GreenChonkException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean isEscaped = false;
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (isEscaped) {
                field.append(character);
                isEscaped = false;
            } else if (character == '\\') {
                isEscaped = true;
            } else if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (isEscaped) {
            throw invalidDataLine(lineNumber);
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Escapes storage delimiter characters inside a user-provided value.
     *
     * @param value the value to escape
     * @return a value safe to store as one field
     */
    private static String escapeDataField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static GreenChonkException invalidDataLine(int lineNumber) {
        return new GreenChonkException("The data file has an invalid task on line " + lineNumber + ".");
    }
}
