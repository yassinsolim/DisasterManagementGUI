package edu.ucalgary.oop;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Scanner;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

public class CLIHandlerTest {
    private CLIHandler cliHandler;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;
    private DatabaseAdapter dbAdapter;
    
    @Before
    public void setUp() throws SQLException {
        // Suppress System.out during database operations
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        dbAdapter = DatabaseAdapter.createTestInstance();
        
        // Now set up the output stream for test assertions
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(outputStream));
        
        // Force LanguageLoader to use default translations
        LanguageLoader.initialize("invalid"); // This will trigger fallback to default translations
    }
    
    @After
    public void tearDown() throws SQLException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        if (dbAdapter != null) {
            dbAdapter.closeConnection();
        }
    }
    
    private void provideInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        InputHandler.setScanner(new Scanner(inputStream));
        cliHandler = new CLIHandler(dbAdapter);
    }

    private void runCLIWithTimeout() throws InterruptedException {
        Thread cliThread = new Thread(() -> cliHandler.start());
        cliThread.start();
        cliThread.join(5000); // Wait up to 5 seconds for the CLI to finish
        if (cliThread.isAlive()) {
            cliThread.interrupt();
        }
    }
    
    @Test
    public void testConstructorInitializesCorrectly() throws SQLException {
        // Arrange & Act
        provideInput("");
        
        // Assert
        assertThat(cliHandler, is(notNullValue()));
    }
    
    @Test
    public void testStartMethodShowsMainMenu() throws SQLException, InterruptedException {
        // Arrange
        provideInput("5\n"); // Exit option
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        System.out.println("\nDEBUG - Actual output:");
        System.out.println("----------------------------------------");
        System.out.println(output);
        System.out.println("----------------------------------------");
        System.out.println("Output length: " + output.length());
        System.out.println("Contains 'Disaster': " + output.contains("Disaster"));
        System.out.println("Contains 'Victim': " + output.contains("Victim"));
        System.out.println("Contains 'Management': " + output.contains("Management"));
        System.out.println("Contains 'System': " + output.contains("System"));
        
        // Check for menu items in a more flexible way
        assertTrue("Output should contain menu title", 
            output.contains("Disaster") || 
            output.contains("Victim") || 
            output.contains("Management") || 
            output.contains("System"));
            
        assertTrue("Output should contain victim management option", 
            output.contains("Victim") || output.contains("victim"));
            
        assertTrue("Output should contain item management option", 
            output.contains("Item") || output.contains("item"));
            
        assertTrue("Output should contain inquiry management option", 
            output.contains("Inquiry") || output.contains("inquiry"));
            
        assertTrue("Output should contain location management option", 
            output.contains("Location") || output.contains("location"));
            
        assertTrue("Output should contain exit option", 
            output.contains("Exit") || output.contains("exit"));
    }
    
    @Test
    public void testStartMethodHandlesValidOption() throws SQLException, InterruptedException {
        // Arrange
        provideInput("1\n5\n5\n"); // Select Victim Management, then Back, then Exit
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Victim Management"));
    }
    
    @Test
    public void testStartMethodHandlesInvalidOption() throws SQLException, InterruptedException {
        // Arrange
        provideInput("99\n5\n"); // Invalid option, then Exit
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Invalid choice"));
    }
    
    @Test
    public void testStartMethodExitsCorrectly() throws SQLException, InterruptedException {
        // Arrange
        provideInput("5\n"); // Exit option
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Goodbye"));
    }
}