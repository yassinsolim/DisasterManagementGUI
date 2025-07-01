package edu.ucalgary.oop;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.time.LocalDate;

public class InputHandlerTest {
    private InputHandler inputHandler;
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream outputStream;
    
    @Before
    public void setUp() {
        inputHandler = new InputHandler();
        originalIn = System.in;
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    
    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        inputHandler.closeScanner();
    }
    
    private void provideInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        inputHandler.setScanner(new java.util.Scanner(System.in));
    }
    
    @Test
    public void testGetNameInputWithValidName() {
        // Arrange
        provideInput("John\n");
        
        // Act
        String result = inputHandler.getNameInput("First Name");
        
        // Assert
        assertThat(result, is("John"));
    }
    
    @Test
    public void testGetNameInputWithInvalidName() {
        // Arrange
        provideInput("123\nJohn\n");
        
        // Act
        String result = inputHandler.getNameInput("First Name");
        
        // Assert
        assertThat(result, is("John"));
    }
    
    @Test
    public void testGetGenderInputWithValidInput() {
        // Arrange
        provideInput("M\n");
        
        // Act
        String result = inputHandler.getGenderInput("Enter gender (M/W/N): ");
        
        // Assert
        assertThat(result, is("Man"));
        assertThat(outputStream.toString(), containsString("Enter gender (M/W/N): "));
    }
    
    @Test
    public void testGetGenderInputWithInvalidGender() {
        // Arrange
        provideInput("X\nM\n");
        
        // Act
        String result = inputHandler.getGenderInput("Gender (M/F/N)");
        
        // Assert
        assertThat(result, is("Man"));
    }
    
    @Test
    public void testGetDateInputWithValidDate() {
        // Arrange
        provideInput("2024-03-20\n");
        
        // Act
        LocalDate result = inputHandler.getDateInput("Date (YYYY-MM-DD)");
        
        // Assert
        assertThat(result, is(LocalDate.of(2024, 3, 20)));
    }
    
    @Test
    public void testGetDateInputWithInvalidDate() {
        // Arrange
        provideInput("invalid\n2024-03-20\n");
        
        // Act
        LocalDate result = inputHandler.getDateInput("Date (YYYY-MM-DD)");
        
        // Assert
        assertThat(result, is(LocalDate.of(2024, 3, 20)));
    }
    
    @Test
    public void testGetPhoneInputWithValidPhone() {
        // Arrange
        provideInput("4035551234\n");
        
        // Act
        String result = inputHandler.getPhoneInput("Phone Number");
        
        // Assert
        assertThat(result, is("4035551234"));
    }
    
    @Test
    public void testGetPhoneInputWithInvalidPhone() {
        // Arrange
        provideInput("invalid\n4035551234\n");
        
        // Act
        String result = inputHandler.getPhoneInput("Phone Number");
        
        // Assert
        assertThat(result, is("4035551234"));
    }
    
    @Test
    public void testGetEmailInputWithValidEmail() {
        // Arrange
        provideInput("test@example.com\n");
        
        // Act
        String result = inputHandler.getEmailInput("Email Address");
        
        // Assert
        assertThat(result, is("test@example.com"));
    }
    
    @Test
    public void testGetEmailInputWithInvalidEmail() {
        // Arrange
        provideInput("invalid\ntest@example.com\n");
        
        // Act
        String result = inputHandler.getEmailInput("Email Address");
        
        // Assert
        assertThat(result, is("test@example.com"));
    }
    
    @Test
    public void testGetYesNoInputWithYes() {
        // Arrange
        provideInput("y\n");
        
        // Act
        boolean result = inputHandler.getYesNoInput("Do you want to continue? (y/n)");
        
        // Assert
        assertThat(result, is(true));
    }
    
    @Test
    public void testGetYesNoInputWithNo() {
        // Arrange
        provideInput("n\n");
        
        // Act
        boolean result = inputHandler.getYesNoInput("Do you want to continue? (y/n)");
        
        // Assert
        assertThat(result, is(false));
    }
    
    @Test
    public void testGetYesNoInputWithInvalidInput() {
        // Arrange
        provideInput("invalid\ny\n");
        
        // Act
        boolean result = inputHandler.getYesNoInput("Do you want to continue? (y/n)");
        
        // Assert
        assertThat(result, is(true));
    }
} 