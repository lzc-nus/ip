/**
 * Represents a task's completion status and the icon used to display it.
 */
public enum TaskStatus {
    NOT_DONE(" "),
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this status.
     *
     * @return the status icon
     */
    public String getIcon() {
        return icon;
    }
}
