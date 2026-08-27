# Command-Line UI Test Plan

Run these tests after each application code change using the project-local `test-ui` skill. Expected values are meaningful output fragments checked in order; startup animation frames are intentionally excluded because they do not affect command behavior.

## Coverage

- Create and list all three task types with parsed, consistently formatted dates.
- Preserve `LocalDate` details while marking and unmarking through `Task` polymorphism.
- Accept valid ISO dates, including leap days, and reject invalid dates without storing a task.
- Reject events that end before they start while allowing same-day events.
- Reject empty and unknown commands with actionable feedback.
- Reject incomplete task commands and invalid task numbers without changing stored tasks.
- Delete tasks from collection storage and renumber the remaining list.
- Reject invalid delete requests without changing stored tasks.
- Create missing storage automatically, save canonical dates on every mutation, and restore formatted dates and statuses.
- Run each automated case in isolated temporary storage so saved tasks cannot leak between cases.

## Persistence check

In a temporary working directory, run Green Chonk twice against the same `data/greenchonk.txt`:

1. Add a todo, deadline, and event; mark the deadline; delete the todo; then exit.
2. Start Green Chonk again and run `list`.

The second session must restore the marked deadline and event, including their parsed dates, while the deleted todo must remain absent. The data file must contain ISO dates even though the UI displays friendly dates. The test directory and data file must not exist before the first session; this verifies first-run creation as well as loading.

Also seed the data file with an event whose ending date is before its starting date. On startup, Green Chonk must identify that line as invalid rather than loading the impossible event.

## Automated cases

```json
{
  "main_class": "GreenChonk",
  "cases": [
    {
      "name": "add-and-list-task-types",
      "aim": "Verify todos, deadlines, and events are constructed and displayed with the correct type icons and details.",
      "inputs": [
        "todo borrow book",
        "deadline return book /by 2026-08-30",
        "event project meeting /from 2026-08-31 /to 2026-09-01",
        "list",
        "bye"
      ],
      "expected": [
        "Chomped this task:\n  [T][ ] borrow book\nGreen Chonk is now carrying 1 task.",
        "Chomped this task:\n  [D][ ] return book (by: Aug 30 2026)\nGreen Chonk is now carrying 2 tasks.",
        "Chomped this task:\n  [E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)\nGreen Chonk is now carrying 3 tasks.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Aug 30 2026)\n3.[E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)"
      ]
    },
    {
      "name": "mark-and-unmark-subtype",
      "aim": "Verify completion changes operate polymorphically without losing deadline details.",
      "inputs": [
        "deadline submit report /by 2026-08-28",
        "mark 1",
        "list",
        "unmark 1",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] submit report (by: Aug 28 2026)",
        "Nice! Green Chonk marked this task as done:\n  [D][X] submit report (by: Aug 28 2026)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][X] submit report (by: Aug 28 2026)",
        "OK, Green Chonk marked this task as not done yet:\n  [D][ ] submit report (by: Aug 28 2026)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] submit report (by: Aug 28 2026)"
      ]
    },
    {
      "name": "parse-and-format-calendar-dates",
      "aim": "Verify valid ISO dates, including a leap day, are stored as dates and displayed in a friendly format.",
      "inputs": [
        "deadline do homework /by 2028-02-29",
        "event orientation /from 2026-12-31 /to 2027-01-01",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] do homework (by: Feb 29 2028)",
        "[E][ ] orientation (from: Dec 31 2026 to: Jan 01 2027)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] do homework (by: Feb 29 2028)\n2.[E][ ] orientation (from: Dec 31 2026 to: Jan 01 2027)"
      ]
    },
    {
      "name": "reject-empty-and-unknown-commands",
      "aim": "Verify empty and unknown inputs report specific errors and leave the task list unchanged.",
      "inputs": [
        "",
        "todo",
        "roll away",
        "list",
        "bye"
      ],
      "expected": [
        "Oops! Green Chonk couldn't chomp that:\n  Please enter a command. Try: todo buy milk",
        "Oops! Green Chonk couldn't chomp that:\n  A todo needs a description. Try: todo buy milk",
        "Oops! Green Chonk couldn't chomp that:\n  I don't recognize \"roll away\". Try todo, deadline, event, list, mark, unmark, delete, or bye.",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "reject-incomplete-scheduled-tasks",
      "aim": "Verify each missing deadline or event field is explained and invalid tasks are not stored.",
      "inputs": [
        "todo valid task",
        "deadline submit report",
        "deadline /by 2026-08-28",
        "deadline submit report /by",
        "event meeting /to 2026-08-29",
        "event /from 2026-08-28 /to 2026-08-29",
        "event meeting /from 2026-08-28",
        "event meeting /from /to 2026-08-29",
        "event meeting /from 2026-08-28 /to",
        "list",
        "bye"
      ],
      "expected": [
        "[T][ ] valid task",
        "A deadline needs /by followed by a date. Try: deadline submit report /by 2026-08-28",
        "A deadline needs a description before /by.",
        "A deadline needs a date after /by.",
        "An event needs /from and /to. Try: event meeting /from 2026-08-28 /to 2026-08-29",
        "An event needs a description before /from.",
        "An event needs /to followed by an ending date.",
        "An event needs a starting date after /from.",
        "An event needs an ending date after /to.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] valid task"
      ]
    },
    {
      "name": "reject-invalid-calendar-dates",
      "aim": "Verify invalid or wrongly formatted deadline and event dates are rejected without changing the task list.",
      "inputs": [
        "todo valid task",
        "deadline impossible date /by 2026-02-29",
        "deadline wrong format /by 29-02-2028",
        "event bad start /from 2026-13-01 /to 2026-08-29",
        "event bad end /from 2026-08-28 /to tomorrow",
        "list",
        "bye"
      ],
      "expected": [
        "[T][ ] valid task",
        "The deadline date must use yyyy-MM-dd and be valid. Try: 2026-08-28",
        "The deadline date must use yyyy-MM-dd and be valid. Try: 2026-08-28",
        "The event start date must use yyyy-MM-dd and be valid. Try: 2026-08-28",
        "The event end date must use yyyy-MM-dd and be valid. Try: 2026-08-29",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] valid task"
      ]
    },
    {
      "name": "validate-event-date-range",
      "aim": "Verify an event cannot end before it starts while equal dates remain valid for a same-day event.",
      "inputs": [
        "event backwards /from 2026-12-29 /to 2023-03-24",
        "event same-day /from 2026-08-28 /to 2026-08-28",
        "list",
        "bye"
      ],
      "expected": [
        "An event's end date cannot be before its start date. Try /to 2026-12-29 or later.",
        "Chomped this task:\n  [E][ ] same-day (from: Aug 28 2026 to: Aug 28 2026)\nGreen Chonk is now carrying 1 task.",
        "Here are the tasks Green Chonk is carrying:\n1.[E][ ] same-day (from: Aug 28 2026 to: Aug 28 2026)"
      ]
    },
    {
      "name": "reject-invalid-task-numbers",
      "aim": "Verify mark and unmark reject missing, non-numeric, and out-of-range task numbers without changing task state.",
      "inputs": [
        "mark 1",
        "unmark",
        "todo keep me incomplete",
        "mark zero",
        "mark 0",
        "mark 2",
        "unmark 2",
        "list",
        "bye"
      ],
      "expected": [
        "There are no tasks to mark yet.",
        "Please provide a task number. Try: unmark 1",
        "[T][ ] keep me incomplete",
        "\"zero\" is not a valid task number. Use a whole number such as 1.",
        "Task 0 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] keep me incomplete"
      ]
    },
    {
      "name": "delete-and-renumber-tasks",
      "aim": "Verify deleting middle, first, and last tasks removes the correct object and keeps numbering contiguous.",
      "inputs": [
        "todo first task",
        "deadline second task /by 2026-08-28",
        "event third task /from 2026-08-29 /to 2026-08-30",
        "mark 2",
        "delete 2",
        "list",
        "delete 1",
        "delete 1",
        "list",
        "bye"
      ],
      "expected": [
        "Noted. Green Chonk removed this task:\n  [D][X] second task (by: Aug 28 2026)\nGreen Chonk is now carrying 2 tasks.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] first task\n2.[E][ ] third task (from: Aug 29 2026 to: Aug 30 2026)",
        "Noted. Green Chonk removed this task:\n  [T][ ] first task\nGreen Chonk is now carrying 1 task.",
        "Noted. Green Chonk removed this task:\n  [E][ ] third task (from: Aug 29 2026 to: Aug 30 2026)\nGreen Chonk is now carrying 0 tasks.",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "reject-invalid-delete-requests",
      "aim": "Verify delete rejects missing, non-numeric, and out-of-range task numbers without removing a task.",
      "inputs": [
        "delete 1",
        "delete",
        "todo keep this task",
        "delete zero",
        "delete 0",
        "delete -1",
        "delete 2",
        "list",
        "bye"
      ],
      "expected": [
        "There are no tasks to delete yet.",
        "Please provide a task number. Try: delete 1",
        "[T][ ] keep this task",
        "\"zero\" is not a valid task number. Use a whole number such as 1.",
        "Task 0 does not exist. Choose a number from 1 to 1.",
        "Task -1 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] keep this task"
      ]
    }
  ]
}
```
