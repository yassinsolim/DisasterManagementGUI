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
        assertTrue(output.contains(LanguageLoader.getTranslation("menu.title")));
        assertTrue(output.contains("1. " + LanguageLoader.getTranslation("menu.victims")));
        assertTrue(output.contains("2. " + LanguageLoader.getTranslation("menu.supplies")));
        assertTrue(output.contains("3. " + LanguageLoader.getTranslation("menu.inquiries")));
        assertTrue(output.contains("4. " + LanguageLoader.getTranslation("menu.locations")));
        assertTrue(output.contains("5. " + LanguageLoader.getTranslation("menu.exit")));
    }
    
    @Test
    public void testStartMethodHandlesValidOption() throws SQLException, InterruptedException {
        // Arrange
        provideInput("1\n5\n5\n"); // Select Victim Management, then Back, then Exit
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("1. " + LanguageLoader.getTranslation("menu.add_victim")));
    }
    
    @Test
    public void testStartMethodHandlesInvalidOption() throws SQLException, InterruptedException {
        // Arrange
        provideInput("99\n5\n"); // Invalid option, then Exit
        
        // Act
        runCLIWithTimeout();
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Please enter a number between 1 and 5."));
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