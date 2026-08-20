# Green Chonk

Green Chonk is a small Java chatbot prototype with a playful personality. It displays a classic ASCII banner, animates its responses with a short thinking sequence, stores tasks in memory, and exits when the user types bye.

The current version stores each ordinary line as a task and confirms it with a Green Chonk-themed acknowledgement. The `list` command displays numbered tasks and their completion status. Use `mark NUMBER` to complete a task and `unmark NUMBER` to make it incomplete again. The `bye` command ends the conversation regardless of capitalization.

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

The program keeps reading commands until the user enters `bye` in any capitalization. Use `list` to display the tasks saved during the current run, `mark NUMBER` to complete a task, and `unmark NUMBER` to reverse that status. Tasks are stored only in memory and are not written to disk. Run the program in an interactive terminal to see the thinking animation overwrite the dots in place. If the output is redirected to a file or captured by a tool, the carriage-return characters may appear as separate frames instead.

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
> buy milk
Chomped this task: buy milk
> finish report
Chomped this task: finish report
> list
Here are the tasks Green Chonk is carrying:
1.[ ] buy milk
2.[ ] finish report
> mark 2
Nice! Green Chonk marked this task as done:
  [X] finish report
> list
Here are the tasks Green Chonk is carrying:
1.[ ] buy milk
2.[X] finish report
> unmark 2
OK, Green Chonk marked this task as not done yet:
  [ ] finish report
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
        ├── GreenChonk.java
        └── Task.java
~~~

Keep Java source files under src/main/java, which is the source directory expected by the project setup.
