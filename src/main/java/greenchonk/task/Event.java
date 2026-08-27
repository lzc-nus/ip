package greenchonk.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description the event description.
     * @param from the event's starting date.
     * @param to the event's ending date.
     * @throws IllegalArgumentException if the ending date is before the starting date.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event's starting date.
     *
     * @return the event's starting date.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns this event's ending date.
     *
     * @return the event's ending date.
     */
    public LocalDate getTo() {
        return to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the task display text with its formatted date range.
     *
     * @return the event's display text
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE_FORMAT)
                + " to: " + to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
