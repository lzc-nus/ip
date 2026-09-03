package greenchonk.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import greenchonk.exception.GreenChonkException;
import greenchonk.exception.StorageException;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.Todo;

class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingDirectoryAndFile_emptyFileCreated() throws GreenChonkException {
        Path dataFile = tempDirectory.resolve("nested/data.txt");
        Storage storage = new Storage(dataFile.toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(Files.isRegularFile(dataFile));
        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypesStatusDatesAndEscapes_roundTripPreserved()
            throws GreenChonkException, IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("pipe | and slash \\");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        deadline.markAsDone();
        Event event = new Event("conference", LocalDate.of(2026, 8, 29),
                LocalDate.of(2026, 8, 30));

        storage.save(new TaskList(List.of(todo, deadline, event)));
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of(
                "T | 0 | pipe \\| and slash \\\\",
                "D | 1 | submit report | 2026-08-28",
                "E | 0 | conference | 2026-08-29 | 2026-08-30"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
        assertEquals(3, loadedTasks.size());
        assertEquals("pipe | and slash \\", loadedTasks.get(0).getDescription());
        assertFalse(loadedTasks.get(0).isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertTrue(loadedDeadline.isDone());
        assertEquals(LocalDate.of(2026, 8, 28), loadedDeadline.getDueDate());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(LocalDate.of(2026, 8, 29), loadedEvent.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 30), loadedEvent.getEndDate());
    }

    @Test
    void load_blankLines_blankLinesIgnored() throws GreenChonkException, IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of("", "T | 0 | task", "   "),
                StandardCharsets.UTF_8);

        List<Task> loadedTasks = new Storage(dataFile.toString()).load();

        assertEquals(1, loadedTasks.size());
        assertEquals("task", loadedTasks.get(0).getDescription());
    }

    @Test
    void load_invalidStoredRepresentations_exceptionThrown() throws IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        Storage storage = new Storage(dataFile.toString());
        List<String> invalidLines = List.of(
                "X | 0 | task",
                "T | 2 | task",
                "T | 0",
                "T | 0 | task | extra",
                "D | 0 | task | tomorrow",
                "E | 0 | event | 2026-08-30 | 2026-08-29",
                "T | 0 | dangling\\");

        for (String invalidLine : invalidLines) {
            Files.writeString(dataFile, invalidLine, StandardCharsets.UTF_8);
            StorageException exception = assertThrows(StorageException.class,
                    storage::load);
            assertEquals("The data file has an invalid task on line 1.",
                    exception.getMessage());
        }
    }

    @Test
    void load_invalidThirdLine_exceptionIdentifiesPhysicalLine() throws IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of("T | 0 | valid", "", "X | 0 | invalid"),
                StandardCharsets.UTF_8);

        StorageException exception = assertThrows(StorageException.class, () ->
                new Storage(dataFile.toString()).load());

        assertEquals("The data file has an invalid task on line 3.",
                exception.getMessage());
    }

    @Test
    void save_parentPathIsAFile_exceptionThrown() throws IOException {
        Path blockingFile = tempDirectory.resolve("blocking-file");
        Files.writeString(blockingFile, "not a directory", StandardCharsets.UTF_8);
        Storage storage = new Storage(blockingFile.resolve("tasks.txt").toString());

        StorageException exception = assertThrows(StorageException.class, () ->
                storage.save(new TaskList()));

        assertTrue(exception.getMessage().startsWith("I couldn't save the task list:"));
    }
}
