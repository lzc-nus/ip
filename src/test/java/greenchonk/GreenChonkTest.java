package greenchonk;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GreenChonkTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_consecutiveCommands_shareTaskState() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String addResponse = greenChonk.getResponse("todo read book");
        String listResponse = greenChonk.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingError() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String response = greenChonk.getResponse("   ");

        assertTrue(response.contains("Please enter a command. Try: todo buy milk"));
    }

    @Test
    void getResponse_exitCommand_returnsFarewellWithoutAnimationFrames() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String response = greenChonk.getResponse("bye");

        assertTrue(response.contains("Bye! I'm rolling off for now. See you again soon!"));
        assertFalse(response.contains("\r"));
        assertFalse(response.contains("___"));
    }
}
