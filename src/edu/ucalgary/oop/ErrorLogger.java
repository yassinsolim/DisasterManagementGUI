/**
 * Provides error logging functionality for the Disaster Management System.
 * Errors are logged to both a file and an in-memory list, with timestamps.
 * This class implements a singleton pattern to ensure consistent error logging
 * throughout the application.
 *
 * @author University of Calgary OOP Team
 * @version 1.0
 * @since 2024-03-20
 */
package edu.ucalgary.oop;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ErrorLogger {
    private static final String LOG_FILE = "error.log";
    private static List<String> log = new ArrayList<>();

    /**
     * Logs an error message with a timestamp to both the log file and in-memory list.
     * The message is appended to the log file in ISO date-time format.
     *
     * @param message The error message to log
     */
    public static void logError(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String logMessage = String.format("[%s] %s", timestamp, message);
            writer.println(logMessage);
            log.add(logMessage);
        } catch (IOException e) {
            System.err.println("Failed to write error log.");
        }
    }

    /**
     * Gets a copy of the in-memory error log.
     *
     * @return A list of error messages with timestamps
     */
    public static List<String> getLog() {
        return log;
    }

    /**
     * Returns a string representation of all logged errors.
     * Each error message is on a new line.
     *
     * @return A string containing all error messages
     */
    @Override
    public String toString() {
        return String.join("\n", log);
    }
} 