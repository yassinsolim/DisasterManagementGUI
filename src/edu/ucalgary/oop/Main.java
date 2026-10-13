/**
 * Main entry point for the Disaster Management System.
 * This class handles the initialization of the system, including language loading
 * and GUI startup.
 *
 * @author University of Calgary OOP Team
 * @version 1.0
 * @since 2024-03-20
 */
package edu.ucalgary.oop;

import javax.swing.SwingUtilities;
import java.util.regex.Pattern;

public class Main {
    private static final Pattern LANG_CODE_PATTERN = Pattern.compile("^[a-z]{2}-[A-Z]{2}$");
    private static final String DEFAULT_LANG = "en-CA";

    /**
     * Main entry point for the application.
     * Initializes the system with the specified language code or defaults to en-CA.
     * Starts the GUI after successful initialization.
     *
     * @param args Command line arguments. If provided, the first argument should be
     *            a language code in the format "aa-BB" (e.g., "en-CA").
     */
    public static void main(String[] args) {
        String langCode = DEFAULT_LANG;
        
        // Process language argument if provided
        if (args.length > 0) {
            String providedLang = args[0];
            if (LANG_CODE_PATTERN.matcher(providedLang).matches()) {
                langCode = providedLang;
            } else {
                System.err.println("Error: Invalid language code format. Expected format: aa-BB (e.g., en-CA)");
                System.err.println("Defaulting to " + DEFAULT_LANG);
            }
        }

        // Initialize language loader
        if (!LanguageLoader.initialize(langCode)) {
            System.err.println("Error: Could not load language file for " + langCode);
            System.err.println("Defaulting to " + DEFAULT_LANG);
            if (!LanguageLoader.initialize(DEFAULT_LANG)) {
                System.err.println("Fatal error: Could not load default language file");
                System.exit(1);
            }
        }

        // Start GUI
        SwingUtilities.invokeLater(() -> {
            new DisasterManagementGUI().setVisible(true);
        });
    }
} 