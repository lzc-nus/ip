package greenchonk;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import greenchonk.command.Command;
import greenchonk.exception.GreenChonkException;
import greenchonk.parser.Parser;
import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final String DATA_FILE_PATH = "data/greenchonk.txt";

    private final Storage storage;
    private final Ui ui;
    private TaskList guiTasks;

    /**
     * Creates Green Chonk with its default data file.
     */
    public GreenChonk() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Green Chonk with a command-line UI and file-backed storage.
     *
     * @param filePath the path of the task data file.
     */
    public GreenChonk(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
    }

    /**
     * Starts Green Chonk with its default data file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new GreenChonk().run();
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        TaskList tasks = loadTasks(ui);
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            try {
                Command command = Parser.parse(ui.readCommand().trim());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (GreenChonkException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Executes one command and returns its user-facing response for the GUI.
     *
     * @param input the command entered by the user.
     * @return the response produced while executing the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        try (PrintStream responseStream = new PrintStream(outputBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(InputStream.nullInputStream(), responseStream);
            if (guiTasks == null) {
                guiTasks = loadTasks(responseUi);
            }

            try {
                Command command = Parser.parse(input.trim());
                command.execute(guiTasks, responseUi, storage);
            } catch (GreenChonkException exception) {
                responseUi.showError(exception.getMessage());
            }
        }
        return outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk, or an empty task list if loading fails.
     */
    private TaskList loadTasks(Ui targetUi) {
        try {
            return new TaskList(storage.load());
        } catch (GreenChonkException exception) {
            targetUi.showLoadingError(exception.getMessage());
            return new TaskList();
        }
    }
}
