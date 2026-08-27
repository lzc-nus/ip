/**
 * Greets the user as Green Chonk, manages tasks, and exits on {@code bye}.
 */
public class GreenChonk {
    private static final String DATA_FILE_PATH = "data/greenchonk.txt";
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates Green Chonk with a command-line UI and file-backed storage.
     *
     * @param filePath the path of the task data file
     */
    public GreenChonk(String filePath) {
        storage = new Storage(filePath);
        ui = new Ui();
    }

    public static void main(String[] args) {
        new GreenChonk(DATA_FILE_PATH).run();
    }

    /**
     * Runs the command loop until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        TaskList tasks = loadTasks();
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
     * Loads saved tasks, creating the data directory and file on first use.
     *
     * @return the tasks restored from disk, or an empty task list if loading fails
     */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (GreenChonkException exception) {
            ui.showLoadingError(exception.getMessage());
            return new TaskList();
        }
    }

}
