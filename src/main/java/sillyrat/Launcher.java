package sillyrat;

import javafx.application.Application;

/**
 * A launcher class for SillyRat application to work around classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
