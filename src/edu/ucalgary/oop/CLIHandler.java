package edu.ucalgary.oop;

import java.util.Scanner;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Handles command-line interface operations for the disaster management system.
 * This class manages user interactions through the console, including menu display,
 * input processing, and command execution.
 */
public class CLIHandler {
    private final Scanner scanner;
    private final DatabaseAdapter dbAdapter;
    private boolean running;
    private InputHandler inputHandler;

    /**
     * Constructs a new CLIHandler with the specified database adapter.
     *
     * @param dbAdapter The database adapter for data operations
     */
    public CLIHandler(DatabaseAdapter dbAdapter) {
        this.scanner = new Scanner(System.in);
        this.dbAdapter = dbAdapter;
        this.running = true;
        this.inputHandler = new InputHandler();
        LanguageLoader.initialize("en-CA");
    }

    /**
     * Starts the CLI application and displays the main menu.
     */
    public void start() {
        while (running) {
            displayMainMenu();
            int choice = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.choice"), 1, 5);
            processMainMenuChoice(choice);
        }
    }

    /**
     * Displays the main menu options to the user.
     */
    private void displayMainMenu() {
        String title = LanguageLoader.getTranslation("menu.title");
        String victims = LanguageLoader.getTranslation("menu.victims");
        String supplies = LanguageLoader.getTranslation("menu.supplies");
        String inquiries = LanguageLoader.getTranslation("menu.inquiries");
        String locations = LanguageLoader.getTranslation("menu.locations");
        String exit = LanguageLoader.getTranslation("menu.exit");
        
        // TODO: replace with proper logger // System.out.println("\nDEBUG - Menu items:");
        // TODO: replace with proper logger // System.out.println("Title: '" + title + "'");
        // TODO: replace with proper logger // System.out.println("Victims: '" + victims + "'");
        // TODO: replace with proper logger // System.out.println("Supplies: '" + supplies + "'");
        // TODO: replace with proper logger // System.out.println("Inquiries: '" + inquiries + "'");
        // TODO: replace with proper logger // System.out.println("Locations: '" + locations + "'");
        // TODO: replace with proper logger // System.out.println("Exit: '" + exit + "'");
        
        System.out.println("\n" + title);
        System.out.println("1. " + victims);
        System.out.println("2. " + supplies);
        System.out.println("3. " + inquiries);
        System.out.println("4. " + locations);
        System.out.println("5. " + exit);
    }

    /**
     * Processes the user's main menu choice and executes the corresponding action.
     *
     * @param choice The user's menu selection
     */
    private void processMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleVictimMenu();
                break;
            case 2:
                handleSupplyMenu();
                break;
            case 3:
                handleInquiryMenu();
                break;
            case 4:
                handleLocationMenu();
                break;
            case 5:
                running = false;
                System.out.println(LanguageLoader.getTranslation("messages.goodbye"));
                break;
            default:
                System.out.println(LanguageLoader.getTranslation("messages.invalid_choice"));
        }
    }

    /**
     * Handles the victim management menu and its operations.
     * Provides options for adding, viewing, updating, and deleting victims.
     */
    private void handleVictimMenu() {
        while (true) {
            System.out.println("\n" + LanguageLoader.getTranslation("menu.victims"));
            System.out.println("1. " + LanguageLoader.getTranslation("menu.add_victim"));
            System.out.println("2. " + LanguageLoader.getTranslation("menu.view_victims"));
            System.out.println("3. " + LanguageLoader.getTranslation("menu.update_victim"));
            System.out.println("4. " + LanguageLoader.getTranslation("menu.delete_victim"));
            System.out.println("5. " + LanguageLoader.getTranslation("menu.back"));

            int choice = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.choice"), 1, 5);
            if (choice == 5) break;
            processVictimMenuChoice(choice);
        }
    }

    /**
     * Processes the user's victim menu choice and executes the corresponding action.
     *
     * @param choice The user's menu selection
     */
    private void processVictimMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addVictim();
                break;
            case 2:
                viewVictims();
                break;
            case 3:
                updateVictim();
                break;
            case 4:
                deleteVictim();
                break;
            default:
                System.out.println(LanguageLoader.getTranslation("messages.invalid_choice"));
        }
    }

    /**
     * Handles the supply management menu and its operations.
     * Provides options for adding, viewing, updating, and deleting supplies.
     */
    private void handleSupplyMenu() {
        while (true) {
            System.out.println("\n" + LanguageLoader.getTranslation("menu.supplies"));
            System.out.println("1. " + LanguageLoader.getTranslation("menu.add_supply"));
            System.out.println("2. " + LanguageLoader.getTranslation("menu.view_supplies"));
            System.out.println("3. " + LanguageLoader.getTranslation("menu.update_supply"));
            System.out.println("4. " + LanguageLoader.getTranslation("menu.delete_supply"));
            System.out.println("5. " + LanguageLoader.getTranslation("menu.back"));

            int choice = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.choice"), 1, 5);
            if (choice == 5) break;
            processSupplyMenuChoice(choice);
        }
    }

    /**
     * Processes the user's supply menu choice and executes the corresponding action.
     *
     * @param choice The user's menu selection
     */
    private void processSupplyMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addSupply();
                break;
            case 2:
                viewSupplies();
                break;
            case 3:
                updateSupply();
                break;
            case 4:
                deleteSupply();
                break;
            default:
                System.out.println(LanguageLoader.getTranslation("messages.invalid_choice"));
        }
    }

    /**
     * Handles the inquiry management menu and its operations.
     * Provides options for adding, viewing, updating, and deleting inquiries.
     */
    private void handleInquiryMenu() {
        while (true) {
            System.out.println("\n" + LanguageLoader.getTranslation("menu.inquiries"));
            System.out.println("1. " + LanguageLoader.getTranslation("menu.add_inquiry"));
            System.out.println("2. " + LanguageLoader.getTranslation("menu.view_inquiries"));
            System.out.println("3. " + LanguageLoader.getTranslation("menu.update_inquiry"));
            System.out.println("4. " + LanguageLoader.getTranslation("menu.delete_inquiry"));
            System.out.println("5. " + LanguageLoader.getTranslation("menu.back"));

            int choice = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.choice"), 1, 5);
            if (choice == 5) break;
            processInquiryMenuChoice(choice);
        }
    }

    /**
     * Processes the user's inquiry menu choice and executes the corresponding action.
     *
     * @param choice The user's menu selection
     */
    private void processInquiryMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addInquiry();
                break;
            case 2:
                viewInquiries();
                break;
            case 3:
                updateInquiry();
                break;
            case 4:
                deleteInquiry();
                break;
            default:
                System.out.println(LanguageLoader.getTranslation("messages.invalid_choice"));
        }
    }

    /**
     * Handles the location management menu and its operations.
     * Provides options for adding, viewing, updating, and deleting locations.
     */
    private void handleLocationMenu() {
        while (true) {
            System.out.println("\n" + LanguageLoader.getTranslation("menu.locations"));
            System.out.println("1. " + LanguageLoader.getTranslation("menu.add_location"));
            System.out.println("2. " + LanguageLoader.getTranslation("menu.view_locations"));
            System.out.println("3. " + LanguageLoader.getTranslation("menu.update_location"));
            System.out.println("4. " + LanguageLoader.getTranslation("menu.delete_location"));
            System.out.println("5. " + LanguageLoader.getTranslation("menu.back"));

            int choice = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.choice"), 1, 5);
            if (choice == 5) break;
            processLocationMenuChoice(choice);
        }
    }

    /**
     * Processes the user's location menu choice and executes the corresponding action.
     *
     * @param choice The user's menu selection
     */
    private void processLocationMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addLocation();
                break;
            case 2:
                viewLocations();
                break;
            case 3:
                updateLocation();
                break;
            case 4:
                deleteLocation();
                break;
            default:
                System.out.println(LanguageLoader.getTranslation("messages.invalid_choice"));
        }
    }

    /**
     * Adds a new victim to the system.
     */
    private void addVictim() {
        try {
            String firstName = inputHandler.getNameInput(LanguageLoader.getTranslation("prompt.victim.first_name"));
            String lastName = inputHandler.getNameInput(LanguageLoader.getTranslation("prompt.victim.last_name"));
            LocalDate dateOfBirth = inputHandler.getDateInput(LanguageLoader.getTranslation("prompt.victim.date_of_birth"));
            String gender = inputHandler.getGenderInput(LanguageLoader.getTranslation("prompt.victim.gender"));
            String comments = inputHandler.getStringInput(LanguageLoader.getTranslation("prompt.victim.comments"), 
                Pattern.compile("^.*$"), "");

            DisasterVictim victim = new DisasterVictim(firstName, lastName, dateOfBirth);
            victim.setGender(gender);
            victim.setComments(comments);

            int victimId = dbAdapter.addVictim(victim);
            if (victimId > 0) {
                System.out.println(LanguageLoader.getTranslation("messages.victim_added"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.victim_add_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Displays all victims in the system.
     */
    private void viewVictims() {
        try {
            List<DisasterVictim> victims = dbAdapter.getAllVictims();
            if (victims.isEmpty()) {
                System.out.println(LanguageLoader.getTranslation("messages.no_victims"));
                return;
            }
            for (DisasterVictim victim : victims) {
                System.out.println(victim.toString());
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Updates an existing victim's information.
     */
    private void updateVictim() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.victim.id"), 1, Integer.MAX_VALUE);
            DisasterVictim victim = dbAdapter.getVictimById(id);
            if (victim == null) {
                System.out.println(LanguageLoader.getTranslation("messages.victim_not_found"));
                return;
            }

            String firstName = inputHandler.getNameInput(LanguageLoader.getTranslation("prompt.victim.first_name"));
            String lastName = inputHandler.getNameInput(LanguageLoader.getTranslation("prompt.victim.last_name"));
            LocalDate dateOfBirth = inputHandler.getDateInput(LanguageLoader.getTranslation("prompt.victim.date_of_birth"));
            String gender = inputHandler.getGenderInput(LanguageLoader.getTranslation("prompt.victim.gender"));
            String comments = inputHandler.getStringInput(LanguageLoader.getTranslation("prompt.victim.comments"), 
                Pattern.compile("^.*$"), "");

            victim.setFirstName(firstName);
            victim.setLastName(lastName);
            victim.setDateOfBirth(dateOfBirth);
            victim.setGender(gender);
            victim.setComments(comments);

            if (dbAdapter.updateVictim(id, victim)) {
                System.out.println(LanguageLoader.getTranslation("messages.victim_updated"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.victim_update_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Deletes a victim from the system.
     */
    private void deleteVictim() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.victim.id"), 1, Integer.MAX_VALUE);
            if (dbAdapter.deleteVictim(id)) {
                System.out.println(LanguageLoader.getTranslation("messages.victim_deleted"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.victim_not_found"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Adds a new supply to the system.
     */
    private void addSupply() {
        try {
            String type = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.supply.type"),
                Pattern.compile("^[a-zA-Z\\s-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_type")
            );
            String comments = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.supply.comments"),
                Pattern.compile("^.*$"),
                LanguageLoader.getTranslation("messages.error.invalid_comments")
            );

            Supply supply = new Supply(type, comments);
            int supplyId = dbAdapter.addSupply(supply);
            if (supplyId > 0) {
                System.out.println(LanguageLoader.getTranslation("messages.supply_added"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.supply_add_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Displays all supplies in the system.
     */
    private void viewSupplies() {
        try {
            List<Supply> supplies = dbAdapter.getAllSupplies();
            if (supplies.isEmpty()) {
                System.out.println(LanguageLoader.getTranslation("messages.no_supplies"));
                return;
            }
            for (Supply supply : supplies) {
                System.out.println(supply.toString());
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Updates an existing supply's information.
     */
    private void updateSupply() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.supply.id"), 1, Integer.MAX_VALUE);
            List<Supply> supplies = dbAdapter.getAllSupplies();
            Supply supply = supplies.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
            
            if (supply == null) {
                System.out.println(LanguageLoader.getTranslation("messages.supply_not_found"));
                return;
            }

            String type = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.supply.type"),
                Pattern.compile("^[a-zA-Z\\s-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_type")
            );
            String comments = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.supply.comments"),
                Pattern.compile("^.*$"),
                LanguageLoader.getTranslation("messages.error.invalid_comments")
            );

            supply.setType(type);
            supply.setComments(comments);

            if (dbAdapter.updateSupply(supply)) {
                System.out.println(LanguageLoader.getTranslation("messages.supply_updated"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.supply_update_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Deletes a supply from the system.
     */
    private void deleteSupply() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.supply.id"), 1, Integer.MAX_VALUE);
            if (dbAdapter.deleteSupply(id)) {
                System.out.println(LanguageLoader.getTranslation("messages.supply_deleted"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.supply_not_found"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Adds a new inquiry to the system.
     */
    private void addInquiry() {
        try {
            String inquirerName = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.inquiry.inquirer_name"),
                Pattern.compile("^[a-zA-Z\\s-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_name")
            );
            String inquirerContact = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.inquiry.inquirer_contact"),
                Pattern.compile("^[\\w\\s@.-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_contact")
            );
            String inquiryDetails = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.inquiry.comments"),
                Pattern.compile("^.*$"),
                LanguageLoader.getTranslation("messages.error.invalid_comments")
            );

            ExternalInquiry inquiry = new ExternalInquiry(inquirerName, inquirerContact, inquiryDetails);
            int inquiryId = dbAdapter.addExternalInquiry(inquiry);
            if (inquiryId > 0) {
                System.out.println(LanguageLoader.getTranslation("messages.inquiry_added"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.inquiry_add_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Displays all inquiries in the system.
     */
    private void viewInquiries() {
        try {
            List<ExternalInquiry> inquiries = dbAdapter.getExternalInquiries();
            if (inquiries.isEmpty()) {
                System.out.println(LanguageLoader.getTranslation("messages.no_inquiries"));
                return;
            }
            for (ExternalInquiry inquiry : inquiries) {
                System.out.println(inquiry.toString());
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Updates an existing inquiry's information.
     */
    private void updateInquiry() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.inquiry.id"), 1, Integer.MAX_VALUE);
            List<ExternalInquiry> inquiries = dbAdapter.getExternalInquiries();
            ExternalInquiry inquiry = inquiries.stream()
                .filter(i -> i.getInquiryID() == id)
                .findFirst()
                .orElse(null);
            
            if (inquiry == null) {
                System.out.println(LanguageLoader.getTranslation("messages.inquiry_not_found"));
                return;
            }

            String status = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.inquiry.status"),
                Pattern.compile("^(pending|resolved)$"),
                LanguageLoader.getTranslation("messages.error.invalid_status")
            );
            inquiry.setStatus(status);

            if (status.equals("resolved")) {
                String resolution = inputHandler.getStringInput(
                    LanguageLoader.getTranslation("prompt.inquiry.resolution"),
                    Pattern.compile("^.*$"),
                    LanguageLoader.getTranslation("messages.error.invalid_resolution")
                );
                inquiry.setResolution(resolution);
            }

            if (dbAdapter.updateExternalInquiry(inquiry)) {
                System.out.println(LanguageLoader.getTranslation("messages.inquiry_updated"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.inquiry_update_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Deletes an inquiry from the system.
     */
    private void deleteInquiry() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.inquiry.id"), 1, Integer.MAX_VALUE);
            if (dbAdapter.deleteInquiry(id)) {
                System.out.println(LanguageLoader.getTranslation("messages.inquiry_deleted"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.inquiry_not_found"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Adds a new location to the system.
     */
    private void addLocation() {
        try {
            String name = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.location.name"),
                Pattern.compile("^[a-zA-Z0-9\\s-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_name")
            );
            String address = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.location.address"),
                Pattern.compile("^[a-zA-Z0-9\\s,-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_address")
            );

            Location location = new Location(name, address);
            if (dbAdapter.addLocation(location)) {
                System.out.println(LanguageLoader.getTranslation("messages.location_added"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.location_add_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Displays all locations in the system.
     */
    private void viewLocations() {
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            if (locations.isEmpty()) {
                System.out.println(LanguageLoader.getTranslation("messages.no_locations"));
                return;
            }
            for (Location location : locations) {
                System.out.println(location.toString());
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Updates an existing location's information.
     */
    private void updateLocation() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.location.id"), 1, Integer.MAX_VALUE);
            List<Location> locations = dbAdapter.getAllLocations();
            Location location = locations.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
            
            if (location == null) {
                System.out.println(LanguageLoader.getTranslation("messages.location_not_found"));
                return;
            }

            String name = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.location.name"),
                Pattern.compile("^[a-zA-Z0-9\\s-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_name")
            );
            String address = inputHandler.getStringInput(
                LanguageLoader.getTranslation("prompt.location.address"),
                Pattern.compile("^[a-zA-Z0-9\\s,-]+$"),
                LanguageLoader.getTranslation("messages.error.invalid_address")
            );
            String room = inputHandler.getRoomInput(
                LanguageLoader.getTranslation("prompt.location.room")
            );
            String grid = inputHandler.getGridInput(
                LanguageLoader.getTranslation("prompt.location.grid")
            );

            location.setName(name);
            location.setAddress(address);
            location.setRoom(room);
            location.setGrid(grid);

            if (dbAdapter.updateLocation(location)) {
                System.out.println(LanguageLoader.getTranslation("messages.location_updated"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + 
                    LanguageLoader.getTranslation("messages.location_update_failed"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    /**
     * Deletes a location from the system.
     */
    private void deleteLocation() {
        try {
            int id = inputHandler.getIntInput(LanguageLoader.getTranslation("prompt.location.id"), 1, Integer.MAX_VALUE);
            if (dbAdapter.deleteLocation(id)) {
                System.out.println(LanguageLoader.getTranslation("messages.location_deleted"));
            } else {
                System.out.println(LanguageLoader.getTranslation("messages.location_not_found"));
            }
        } catch (Exception e) {
            System.out.println(LanguageLoader.getTranslation("messages.error") + ": " + e.getMessage());
        }
    }

    private void handleAddMedicalRecord(int victimId) throws SQLException {
        String treatment = InputHandler.getStringInput("Treatment Details: ", Pattern.compile("^.+$"), "Please enter treatment details");
        LocalDate date = InputHandler.getDateInput("Date of Treatment (YYYY-MM-DD): ");
        
        // Get available locations
        List<Location> locations = dbAdapter.getAllLocations();
        if (locations.isEmpty()) {
            System.out.println("\nNo locations available. Please add a location first.\n");
            return;
        }
        
        System.out.println("\nAvailable Locations:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i).getName() + " - " + locations.get(i).getAddress());
        }
        
        int locationChoice = InputHandler.getIntInput("Select location (1-" + locations.size() + "): ", 1, locations.size());
        int locationId = locations.get(locationChoice - 1).getLocationId();
        
        if (dbAdapter.addMedicalRecord(victimId, treatment, date, locationId)) {
            System.out.println("\nMedical record added successfully.\n");
        } else {
            System.out.println("\nFailed to add medical record.\n");
        }
    }

    private void handleAssignLocation(int victimId) throws SQLException {
        List<Location> locations = dbAdapter.getAllLocations();
        if (locations.isEmpty()) {
            System.out.println("\nNo locations available. Please add a location first.\n");
            return;
        }
        
        System.out.println("\nAvailable Locations:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i).getName() + " - " + locations.get(i).getAddress());
        }
        
        int locationChoice = InputHandler.getIntInput("Select location (1-" + locations.size() + "): ", 1, locations.size());
        int locationId = locations.get(locationChoice - 1).getLocationId();
        
        if (dbAdapter.assignLocation(victimId, locationId)) {
            System.out.println("\nLocation assigned successfully.\n");
        } else {
            System.out.println("\nFailed to assign location.\n");
        }
    }

    private void handleAssignPersonToLocation() throws SQLException {
        List<DisasterVictim> victims = dbAdapter.getAllVictims();
        if (victims.isEmpty()) {
            System.out.println("No victims found in the system.");
            return;
        }

        int victimIndex = InputHandler.selectFromList("Select a victim to assign:", victims);
        if (victimIndex == -1) return;

        List<Location> locations = dbAdapter.getAllLocations();
        if (locations.isEmpty()) {
            System.out.println("No locations found in the system.");
            return;
        }

        int locationIndex = InputHandler.selectFromList("Select a location to assign:", locations);
        if (locationIndex == -1) return;

        DisasterVictim victim = victims.get(victimIndex);
        Location location = locations.get(locationIndex);

        if (dbAdapter.assignPersonToLocation(victim.getId(), location.getLocationId())) {
            System.out.println("Victim assigned to location successfully.");
        } else {
            System.out.println("Failed to assign victim to location.");
        }
    }

    private void handleViewPersonLocations() throws SQLException {
        List<DisasterVictim> victims = dbAdapter.getAllVictims();
        if (victims.isEmpty()) {
            System.out.println("No victims found in the system.");
            return;
        }

        int victimIndex = InputHandler.selectFromList("Select a victim to view locations:", victims);
        if (victimIndex == -1) return;

        DisasterVictim victim = victims.get(victimIndex);
        List<Location> locations = dbAdapter.getPersonLocations(victim.getId());

        if (locations.isEmpty()) {
            System.out.println("No locations found for this victim.");
            return;
        }

        System.out.println("\n=== Locations for " + victim.getFirstName() + " " + victim.getLastName() + " ===");
        for (Location location : locations) {
            displayLocation(location);
        }
    }

    private void displayVictimList(List<DisasterVictim> victims) {
        if (victims.isEmpty()) {
            System.out.println("\nNo victims found.");
            return;
        }
        
        System.out.println("\nVictims found:");
        System.out.println("-----------------");
        for (DisasterVictim victim : victims) {
            System.out.println(victim.getFirstName() + " " + victim.getLastName() + 
                             " (ID: " + victim.getId() + ")");
        }
        System.out.println("-----------------");
    }

    private void displayVictim(DisasterVictim victim) {
        System.out.println("\n=== Victim Details ===");
        System.out.println("ID: " + victim.getId());
        System.out.println("Name: " + victim.getFirstName() + " " + victim.getLastName());
        System.out.println("Date of Birth: " + victim.getDateOfBirth());
        System.out.println("Gender: " + victim.getGender());
        System.out.println("Entry Date: " + victim.getEntryDate());
        System.out.println("Phone: " + (victim.getPhoneNumber() != null ? victim.getPhoneNumber() : "N/A"));
        System.out.println("Comments: " + (victim.getComments() != null ? victim.getComments() : "N/A"));
        
        // Display location information
        try {
            List<Location> locations = dbAdapter.getPersonLocations(victim.getId());
            if (!locations.isEmpty()) {
                System.out.println("\nCurrent Location(s):");
                for (Location location : locations) {
                    System.out.println("- " + location.getName() + " (" + location.getAddress() + ")");
                }
            } else {
                System.out.println("\nCurrent Location: Not assigned");
            }
        } catch (SQLException e) {
            System.out.println("\nCurrent Location: Error retrieving location information");
        }
        
        // Display medical records
        try {
            List<MedicalRecord> records = dbAdapter.getMedicalRecords(victim.getId());
            if (!records.isEmpty()) {
                System.out.println("\nMedical Records:");
                for (MedicalRecord record : records) {
                    System.out.println("- " + record.getTreatmentDetails() + " (Date: " + record.getDateOfTreatment().toLocalDate() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("\nMedical Records: Error retrieving medical records");
        }
        
        System.out.println("=====================\n");
    }

    private void displaySupply(Supply supply) {
        System.out.printf("ID: %d, Type: %s, Comments: %s%n",
            supply.getId(),
            supply.getType(),
            supply.getComments()
        );
    }

    private void displayInquiry(Inquiry inquiry) {
        System.out.println("\n=== Inquiry Details ===");
        System.out.println("ID: " + inquiry.getId());
        System.out.println("Comments: " + inquiry.getComments());
        System.out.println("Date: " + inquiry.getDateOfInquiry());
        if (inquiry.getInquirerId() != null) {
            System.out.println("Inquirer ID: " + inquiry.getInquirerId());
        }
        if (inquiry.getSeekingId() != null) {
            System.out.println("Seeking ID: " + inquiry.getSeekingId());
        }
        if (inquiry.getLocationId() != null) {
            System.out.println("Location ID: " + inquiry.getLocationId());
        }
        System.out.println("=====================\n");
    }

    private void displayLocation(Location location) {
        System.out.println("\nLocation ID: " + location.getLocationId());
        System.out.println("Name: " + location.getName());
        System.out.println("Address: " + location.getAddress());
    }

    private void logError(String message) {
        try (FileWriter writer = new FileWriter("data/errorlog.txt", true)) {
            writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to error log: " + e.getMessage());
        }
    }
} 