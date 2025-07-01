package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles user input validation and processing for the disaster management system.
 * This class provides methods for getting and validating different types of user input
 * such as strings, integers, dates, and other specific data types.
 */
public class InputHandler {
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final Pattern GENDER_PATTERN = Pattern.compile("^[MWN]$", Pattern.CASE_INSENSITIVE);
    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s-']{2,50}$");
    public static final Pattern ROOM_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    public static final Pattern GRID_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Gets a string input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @param pattern The regex pattern to validate against
     * @param errorMessage The error message to display if validation fails
     * @return The validated string input
     */
    public static String getStringInput(String prompt, Pattern pattern, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (pattern.matcher(input).matches()) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    /**
     * Gets an integer input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The validated integer input
     */
    public static int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /**
     * Gets a date input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated LocalDate input
     */
    public static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid date in the format YYYY-MM-DD.");
            }
        }
    }

    /**
     * Gets a gender input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated gender input
     */
    public static String getGenderInput(String prompt) {
        String input = getStringInput(prompt, GENDER_PATTERN, "Please enter M (Man), W (Woman), or N (Non-binary person)");
        switch (input.toUpperCase()) {
            case "M": return "Man";
            case "W": return "Woman";
            case "N": return "Non-binary person";
            default: return input;
        }
    }

    /**
     * Gets a name input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated name input
     */
    public static String getNameInput(String prompt) {
        return getStringInput(prompt, NAME_PATTERN, 
            "Please enter a valid name (2-50 characters, letters, spaces, hyphens, and apostrophes only).");
    }

    /**
     * Gets a room input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated room input
     */
    public static String getRoomInput(String prompt) {
        return getStringInput(prompt, ROOM_PATTERN, "Please enter a valid room identifier (letters and numbers only)");
    }

    /**
     * Gets a grid input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated grid input
     */
    public static String getGridInput(String prompt) {
        return getStringInput(prompt, GRID_PATTERN, "Please enter a valid grid identifier (letters and numbers only)");
    }

    /**
     * Gets a phone number input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated phone number input
     */
    public static String getPhoneInput(String prompt) {
        return getStringInput(prompt, PHONE_PATTERN, 
            "Please enter a valid 10-digit phone number.");
    }

    /**
     * Gets an email input from the user with validation.
     *
     * @param prompt The prompt to display to the user
     * @return The validated email input
     */
    public static String getEmailInput(String prompt) {
        return getStringInput(prompt, EMAIL_PATTERN, 
            "Please enter a valid email address.");
    }

    /**
     * Selects an item from a list and returns the index of the selected item.
     *
     * @param prompt The prompt to display to the user
     * @param items The list of items to select from
     * @return The index of the selected item
     */
    public static int selectFromList(String prompt, List<?> items) {
        if (items.isEmpty()) {
            System.out.println("No items available to select from.");
            return -1;
        }

        System.out.println(prompt);
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }

        return getIntInput("Enter your choice (1-" + items.size() + "): ", 1, items.size()) - 1;
    }

    /**
     * Gets a yes/no input from the user.
     *
     * @param prompt The prompt to display to the user
     * @return true if the user enters 'y' or 'Y', false otherwise
     */
    public static boolean getYesNoInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("n")) {
                return input.equals("y");
            }
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    /**
     * Gets the current Scanner instance.
     *
     * @return The current Scanner instance
     */
    public static Scanner getScanner() {
        return scanner;
    }

    /**
     * Sets the Scanner instance for input handling.
     *
     * @param newScanner The new Scanner instance to use
     */
    public static void setScanner(Scanner newScanner) {
        if (scanner != null) {
            scanner.close();
        }
        scanner = newScanner;
    }

    /**
     * Closes the scanner and releases its resources.
     */
    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
            scanner = null;
        }
    }
} 