/**
 * Greets the user as Green Chonk and exits.
 */
public class GreenChonk {
    private static final int BANNER_WIDTH = 61;
    private static final long FRAME_DELAY_MILLIS = 280;
    private static final String DIVIDER = "_".repeat(BANNER_WIDTH);

    private static final String BANNER = """
                  ____                       ____ _                 _
                 / ___|_ __ ___  ___ _ __   / ___| |__   ___  _ __ | | __
                | |  _| '__/ _ \\/ _ \\ '_ \\ | |   | '_ \\ / _ \\| '_ \\| |/ /
                | |_| | | |  __/  __/ | | || |___| | | | (_) | | | |   <
                 \\____|_|  \\___|\\___|_| |_| \\____|_| |_|\\___/|_| |_|\\_|\\_\\
                """;

    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println();
        animateMessage("Green Chonk is waking up...");
        animateMessage("Hello! I'm Green Chonk.");
        animateMessage("Ready to chomp through your tasks!");
        animateMessage("What can I do for you?");
        System.out.println();
        System.out.println(DIVIDER);
        animateMessage("Bye! I'm rolling off for now. See you again soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Prints a message after a short, centered thinking animation.
     *
     * @param message the message to reveal after the animation
     */
    private static void animateMessage(String message) {
        for (int dotCount = 1; dotCount <= 3; dotCount++) {
            System.out.print("\r" + center(".".repeat(dotCount)));
            System.out.flush();
            try {
                Thread.sleep(FRAME_DELAY_MILLIS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.print("\r" + center(message));
        System.out.println();
    }

    /**
     * Centers text using the width of the ASCII banner.
     *
     * @param text the text to center
     * @return the text with leading spaces added
     */
    private static String center(String text) {
        int leftPadding = Math.max(0, (BANNER_WIDTH - text.length()) / 2);
        return " ".repeat(leftPadding) + text;
    }
}
