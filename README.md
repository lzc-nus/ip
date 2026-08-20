# Green Chonk

Green Chonk is a small Java chatbot prototype with a playful personality. It displays a classic ASCII banner, animates its responses with a short thinking sequence, tracks typed tasks in memory, and exits when the user types bye.

The current version supports todos, deadlines, and events. Each type displays its own icon alongside its completion status. The `list` command displays numbered tasks, `mark NUMBER` completes a task, `unmark NUMBER` makes it incomplete again, and `delete NUMBER` removes it. Invalid commands produce a specific correction instead of terminating the program. The `bye` command ends the conversation regardless of capitalization.

## Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA (optional)

On macOS with SDKMAN, select the required Java version before building:

~~~bash
sdk use java 25.0.3.fx-zulu
~~~

## Run in IntelliJ IDEA

1. Open this project directory in IntelliJ IDEA.
2. Configure the project SDK and language level to JDK 25.
3. Open src/main/java/GreenChonk.java.
4. Right-click the file and select **Run GreenChonk.main()**.

## Run from a terminal

From the project root:

~~~bash
mkdir -p out
javac -d out src/main/java/*.java
java -cp out GreenChonk
~~~

The program keeps reading commands until the user enters `bye` in any capitalization. Add tasks with `todo DESCRIPTION`, `deadline DESCRIPTION /by DATE`, or `event DESCRIPTION /from START /to END`. Date and time values are stored as entered. Use `list` to display tasks, `mark NUMBER` to complete one, `unmark NUMBER` to reverse that status, and `delete NUMBER` to remove one. The remaining tasks are renumbered automatically. If a command is incomplete, unknown, or refers to a task that does not exist, Green Chonk explains how to correct it and continues running without changing the task list. Tasks are stored only in memory and are not written to disk. Run the program in an interactive terminal to see the thinking animation overwrite the dots in place. If the output is redirected to a file or captured by a tool, the carriage-return characters may appear as separate frames instead.

## Example interaction

The dots are animated in place during a real run, then resolve into centered messages:
Lines beginning with `>` represent user input; Green Chonk does not print the `>` prompt.

~~~text
_____________________________________________________________

                 Green Chonk is waking up...
                   Hello! I'm Green Chonk.
             Ready to chomp through your tasks!
                   What can I do for you?

_____________________________________________________________
> todo buy milk
Chomped this task:
  [T][ ] buy milk
Green Chonk is now carrying 1 task.
> deadline finish report /by Friday 5pm
Chomped this task:
  [D][ ] finish report (by: Friday 5pm)
Green Chonk is now carrying 2 tasks.
> event project meeting /from Monday 2pm /to 4pm
Chomped this task:
  [E][ ] project meeting (from: Monday 2pm to: 4pm)
Green Chonk is now carrying 3 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][ ] finish report (by: Friday 5pm)
3.[E][ ] project meeting (from: Monday 2pm to: 4pm)
> mark 2
Nice! Green Chonk marked this task as done:
  [D][X] finish report (by: Friday 5pm)
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][X] finish report (by: Friday 5pm)
3.[E][ ] project meeting (from: Monday 2pm to: 4pm)
> unmark 2
OK, Green Chonk marked this task as not done yet:
  [D][ ] finish report (by: Friday 5pm)
> delete 2
Noted. Green Chonk removed this task:
  [D][ ] finish report (by: Friday 5pm)
Green Chonk is now carrying 2 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[E][ ] project meeting (from: Monday 2pm to: 4pm)
> todo
Oops! Green Chonk couldn't chomp that:
  A todo needs a description. Try: todo buy milk
> roll away
Oops! Green Chonk couldn't chomp that:
  I don't recognize "roll away". Try todo, deadline, event, list, mark, unmark, delete, or bye.
> bye

_____________________________________________________________
      Bye! I'm rolling off for now. See you again soon!
_____________________________________________________________
~~~

## Project structure

~~~text
src/
└── main/
    └── java/
        ├── Deadline.java
        ├── Event.java
        ├── GreenChonk.java
        ├── GreenChonkException.java
        ├── Task.java
        ├── TaskStatus.java
        └── Todo.java
~~~

Keep Java source files under src/main/java, which is the source directory expected by the project setup.
