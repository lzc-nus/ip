# Command-Line UI Test Plan

Run these tests after each application code change using the project-local `test-ui` skill. Expected values are meaningful output fragments checked in order; startup animation frames are intentionally excluded because they do not affect command behavior.

## Coverage

- Create and list all three Level 4 task types.
- Preserve type-specific details while marking and unmarking through `Task` polymorphism.
- Treat deadline and event date/time values as user-provided strings.

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
    }
  ]
}
```

Malformed commands and missing delimiters are intentionally excluded from Level 4. Add those cases when Level 5 introduces user-facing error handling.
