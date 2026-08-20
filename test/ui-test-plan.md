# Command-Line UI Test Plan

Run these tests after each application code change using the project-local `test-ui` skill. Expected values are meaningful output fragments checked in order; startup animation frames are intentionally excluded because they do not affect command behavior.

## Coverage

- Create and list all three Level 4 task types.
- Preserve type-specific details while marking and unmarking through `Task` polymorphism.
- Treat deadline and event date/time values as user-provided strings.
- Reject empty and unknown commands with actionable feedback.
- Reject incomplete task commands and invalid task numbers without changing stored tasks.

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
        "deadline return book /by Sunday",
        "event project meeting /from Mon 2pm /to 4pm",
        "list",
        "bye"
      ],
      "expected": [
        "Chomped this task:\n  [T][ ] borrow book\nGreen Chonk is now carrying 1 task.",
        "Chomped this task:\n  [D][ ] return book (by: Sunday)\nGreen Chonk is now carrying 2 tasks.",
        "Chomped this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nGreen Chonk is now carrying 3 tasks.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Sunday)\n3.[E][ ] project meeting (from: Mon 2pm to: 4pm)"
      ]
    },
    {
      "name": "mark-and-unmark-subtype",
      "aim": "Verify completion changes operate polymorphically without losing deadline details.",
      "inputs": [
        "deadline submit report /by Friday 5pm",
        "mark 1",
        "list",
        "unmark 1",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] submit report (by: Friday 5pm)",
        "Nice! Green Chonk marked this task as done:\n  [D][X] submit report (by: Friday 5pm)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][X] submit report (by: Friday 5pm)",
        "OK, Green Chonk marked this task as not done yet:\n  [D][ ] submit report (by: Friday 5pm)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] submit report (by: Friday 5pm)"
      ]
    },
    {
      "name": "preserve-free-form-time-values",
      "aim": "Verify Level 4 stores date and time values exactly as strings rather than parsing them.",
      "inputs": [
        "deadline do homework /by no idea :-p",
        "event orientation /from someday /to much later",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] do homework (by: no idea :-p)",
        "[E][ ] orientation (from: someday to: much later)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] do homework (by: no idea :-p)\n2.[E][ ] orientation (from: someday to: much later)"
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
        "Oops! Green Chonk couldn't chomp that:\n  I don't recognize \"roll away\". Try todo, deadline, event, list, mark, unmark, or bye.",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "reject-incomplete-scheduled-tasks",
      "aim": "Verify each missing deadline or event field is explained and invalid tasks are not stored.",
      "inputs": [
        "todo valid task",
        "deadline submit report",
        "deadline /by Friday",
        "deadline submit report /by",
        "event meeting /to 4pm",
        "event /from 2pm /to 4pm",
        "event meeting /from 2pm",
        "event meeting /from /to 4pm",
        "event meeting /from 2pm /to",
        "list",
        "bye"
      ],
      "expected": [
        "[T][ ] valid task",
        "A deadline needs /by followed by a date or time. Try: deadline submit report /by Friday 5pm",
        "A deadline needs a description before /by.",
        "A deadline needs a date or time after /by.",
        "An event needs /from and /to. Try: event meeting /from Monday 2pm /to 4pm",
        "An event needs a description before /from.",
        "An event needs /to followed by an ending date or time.",
        "An event needs a starting date or time after /from.",
        "An event needs an ending date or time after /to.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] valid task"
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
    }
  ]
}
```
