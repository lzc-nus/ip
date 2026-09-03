package greenchonk.task;

import java.time.LocalDate;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an incomplete event with the given description and time range.
     *
     * @param description the event description.
     * @param startDate the event's starting date.
     * @param endDate the event's ending date.
     * @throws IllegalArgumentException if the ending date is before the starting date.
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("An event cannot end before it starts.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns this event's starting date.
     *
     * @return the event's starting date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns this event's ending date.
     *
     * @return the event's ending date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
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
        return super.toString() + " (from: " + TaskDate.format(startDate)
                + " to: " + TaskDate.format(endDate) + ")";
    }
}
