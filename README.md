# Green Chonk

Green Chonk is a small Java chatbot prototype with a playful personality. It displays a classic ASCII banner, animates its responses with a short thinking sequence, greets the user, echoes commands, and exits when the user types bye.

The current version echoes each command exactly as entered. The bye command ends the conversation regardless of capitalization; other commands are not processed yet.

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
javac -d out src/main/java/GreenChonk.java
java -cp out GreenChonk
~~~

The program keeps reading commands until the user enters bye in any capitalization. Run it in an interactive terminal to see the thinking animation overwrite the dots in place. If the output is redirected to a file or captured by a tool, the carriage-return characters may appear as separate frames instead.

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
> hello
hello
> status report
status report
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
        └── GreenChonk.java
~~~

Keep Java source files under src/main/java, which is the source directory expected by the project setup.
