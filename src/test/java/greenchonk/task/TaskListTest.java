package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void constructor_emptyList_listIsEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Task originalTask = new Todo("original");
        List<Task> source = new ArrayList<>(List.of(originalTask));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
        assertSame(originalTask, tasks.get(0));
    }

    @Test
    void add_appendAndInsert_tasksRemainInRequestedOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task insertedTask = new Todo("inserted");
        TaskList tasks = new TaskList();

        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(1, insertedTask);

        assertFalse(tasks.isEmpty());
        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(insertedTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    void delete_existingTask_taskRemovedAndReturned() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }
}
