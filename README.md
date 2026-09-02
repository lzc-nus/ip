# Green Chonk

Green Chonk is a JavaFX task companion with a focused chat interface and a playful personality. It keeps the original command-line interface as a testable fallback, saves typed tasks between sessions, and exits when the user types bye.

The current version supports todos, deadlines, and events. Deadlines and events accept ISO calendar dates such as `2026-08-28` and display them in a friendlier form such as `Aug 28 2026`. Each task type displays its own icon alongside its completion status. The `list` command displays numbered tasks, `find KEYWORD` searches task descriptions, `schedule DATE` finds deadlines and events occurring on a date, `mark NUMBER` completes a task, `unmark NUMBER` makes it incomplete again, and `delete NUMBER` removes it. Invalid commands produce a specific correction instead of terminating the program. The `bye` command ends the conversation regardless of capitalization.

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
3. Open src/main/java/greenchonk/gui/Launcher.java.
4. Right-click the file and select **Run Launcher.main()**.

## Build and run with Gradle

From the project root:

~~~bash
./gradlew clean build
./gradlew run
~~~

The Gradle wrapper downloads the required Gradle version and project dependencies automatically on first use.

To run the original command-line interface instead:

~~~bash
./gradlew runCli
~~~

## Run automated tests

Run the JUnit regression suite from the project root:

~~~bash
./gradlew test
~~~

The suite covers command execution and rollback, parser validation, storage round trips, and task-domain behavior.

## Run static analysis

Check all production and test code against the SE-EDU Java coding standard:

~~~bash
./gradlew checkstyleMain checkstyleTest
~~~

The regular `build` task also runs these Checkstyle checks before producing an artifact.

## Package as an executable JAR

Create the distributable fat JAR from the project root:

~~~bash
./gradlew clean shadowJar
~~~

The generated file is `build/libs/greenchonk.jar`. To test the package as a user would receive it, copy the JAR
into an empty folder, open a terminal in that folder, and run the following command to launch the GUI:

~~~bash
java -jar "greenchonk.jar"
~~~

Keep the generated JAR out of Git; Gradle can reproduce it from the committed source and build configuration.

The program keeps reading commands until the user enters `bye` in any capitalization. Add tasks with `todo DESCRIPTION`, `deadline DESCRIPTION /by DATE`, or `event DESCRIPTION /from START /to END`. Enter deadline and event dates in the `yyyy-MM-dd` format; invalid calendar dates are rejected without storing a task. An event's ending date must be the same as or later than its starting date. Green Chonk displays valid dates as `MMM dd yyyy`. Use `list` to display every task. Use `find KEYWORD` for a case-insensitive substring search of task descriptions; matching results retain their order and original task numbers. Use `schedule DATE` to display deadlines due and events in progress on that date. Scheduled results keep their original task numbers so they can be marked, unmarked, or deleted directly. Use `mark NUMBER` to complete a task, `unmark NUMBER` to reverse that status, and `delete NUMBER` to remove one. The remaining tasks are renumbered automatically. If a command is incomplete, unknown, or refers to a task that does not exist, Green Chonk explains how to correct it and continues running without changing the task list. Tasks and their canonical ISO dates are saved automatically in `data/greenchonk.txt` whenever the list changes and restored the next time the program starts. The folder and file are created automatically on first use. Run the program in an interactive terminal to see the thinking animation overwrite the dots in place. If the output is redirected to a file or captured by a tool, the carriage-return characters may appear as separate frames instead.

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
> deadline finish report /by 2026-08-28
Chomped this task:
  [D][ ] finish report (by: Aug 28 2026)
Green Chonk is now carrying 2 tasks.
> event project meeting /from 2026-08-29 /to 2026-08-30
Chomped this task:
  [E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
Green Chonk is now carrying 3 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][ ] finish report (by: Aug 28 2026)
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> schedule 2026-08-29
Here are the tasks scheduled for 2026-08-29:
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> mark 2
Nice! Green Chonk marked this task as done:
  [D][X] finish report (by: Aug 28 2026)
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[D][X] finish report (by: Aug 28 2026)
3.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> find report
Here are the matching tasks in your list:
2.[D][X] finish report (by: Aug 28 2026)
> unmark 2
OK, Green Chonk marked this task as not done yet:
  [D][ ] finish report (by: Aug 28 2026)
> delete 2
Noted. Green Chonk removed this task:
  [D][ ] finish report (by: Aug 28 2026)
Green Chonk is now carrying 2 tasks.
> list
Here are the tasks Green Chonk is carrying:
1.[T][ ] buy milk
2.[E][ ] project meeting (from: Aug 29 2026 to: Aug 30 2026)
> todo
Oops! Green Chonk couldn't chomp that:
  A todo needs a description. Try: todo buy milk
> roll away
Oops! Green Chonk couldn't chomp that:
  I don't recognize "roll away". Try todo, deadline, event, list, find, schedule, mark, unmark, delete, or bye.
> bye

_____________________________________________________________
      Bye! I'm rolling off for now. See you again soon!
_____________________________________________________________
~~~

## Project structure

~~~text
src/
└── main/
    ├── java/
    │   └── greenchonk/
    │       ├── GreenChonk.java
    │       ├── command/
    │       │   ├── AddCommand.java
    │       │   ├── Command.java
    │       │   ├── DeleteCommand.java
    │       │   ├── ExitCommand.java
    │       │   ├── FindCommand.java
    │       │   ├── ListCommand.java
    │       │   ├── ScheduleCommand.java
    │       │   └── UpdateStatusCommand.java
    │       ├── exception/
    │       │   └── GreenChonkException.java
    │       ├── gui/
    │       │   ├── DialogBox.java
    │       │   ├── Launcher.java
    │       │   ├── Main.java
    │       │   └── MainWindow.java
    │       ├── parser/
    │       │   └── Parser.java
    │       ├── storage/
    │       │   └── Storage.java
    │       ├── task/
    │       │   ├── Deadline.java
    │       │   ├── Event.java
    │       │   ├── Task.java
    │       │   ├── TaskList.java
    │       │   ├── TaskStatus.java
    │       │   └── Todo.java
    │       └── ui/
    │           └── Ui.java
    └── resources/
        └── view/
            ├── DialogBox.fxml
            ├── MainWindow.fxml
            └── styles.css
~~~

Keep Java source files under src/main/java, which is the source directory expected by the project setup.
