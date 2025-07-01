package edu.ucalgary.oop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.lang.reflect.Field;

public class ErrorLoggerTest {
    private static final String TEST_LOG_FILE = "error.log";
    private static File logFile;

    @BeforeClass
    public static void setUpClass() {
        logFile = new File(TEST_LOG_FILE);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Before
    public void setUp() throws Exception {
        // Clear the static log list before each test
        Field logField = ErrorLogger.class.getDeclaredField("log");
        logField.setAccessible(true);
        ((List<?>) logField.get(null)).clear();
        
        // Delete the log file before each test
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    @Test
    public void testLogError() {
        String testMessage = "Test error message";
        ErrorLogger.logError(testMessage);
        
        assertTrue("Log file should exist", logFile.exists());
        List<String> log = ErrorLogger.getLog();
        assertFalse("Log should not be empty", log.isEmpty());
        assertTrue("Log should contain test message", log.get(0).contains(testMessage));
    }

    @Test
    public void testMultipleErrors() {
        String message1 = "First error";
        String message2 = "Second error";
        
        ErrorLogger.logError(message1);
        ErrorLogger.logError(message2);
        
        List<String> log = ErrorLogger.getLog();
        assertEquals("Log should contain two messages", 2, log.size());
        assertTrue("Log should contain first message", log.get(0).contains(message1));
        assertTrue("Log should contain second message", log.get(1).contains(message2));
    }

    @Test
    public void testNullMessage() {
        ErrorLogger.logError(null);
        List<String> log = ErrorLogger.getLog();
        assertFalse("Log should not be empty", log.isEmpty());
        assertTrue("Log should contain null message", log.get(0).contains("null"));
    }

    @Test
    public void testGetLog() {
        String testMessage = "Test getLog";
        ErrorLogger.logError(testMessage);
        
        List<String> log = ErrorLogger.getLog();
        assertFalse("Log should not be empty", log.isEmpty());
        assertTrue("Log should contain test message", log.get(0).contains(testMessage));
    }
} 