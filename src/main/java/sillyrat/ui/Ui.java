package sillyrat.ui;

import java.util.Scanner;

/**
 * This class handles user interface interactions, including displaying messages and reading user input.
 */
public class Ui {
    private static final String LINE =
            "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Initializes a new Ui object by creating a Scanner object to read user input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays a line separator for UI formatting.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads a line of user input and returns it as a String.
     * @return The user input as a String.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Closes the Scanner object used for reading user input to end the session.
     */
    public void close() {
        scanner.close();
    }
}