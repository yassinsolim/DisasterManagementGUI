package edu.ucalgary.oop;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LanguageLoaderTest {
    private static final String TEST_LANG_CODE = "xx-XX";
    private static final String TEST_XML_CONTENT = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<translations>\n" +
        "    <menu>\n" +
        "        <title>Test Title</title>\n" +
        "    </menu>\n" +
        "    <messages>\n" +
        "        <error>Test Error</error>\n" +
        "    </messages>\n" +
        "</translations>";
    private File testFile;
    private Path dataDir;

    @Before
    public void setUp() throws IOException {
        // Create test data directory
        dataDir = Files.createTempDirectory("test_data");
        File dataDirFile = dataDir.toFile();
        dataDirFile.mkdir();
        
        // Create test language file
        testFile = new File(dataDirFile, TEST_LANG_CODE + ".xml");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write(TEST_XML_CONTENT);
        }

        // Set the data directory path as a system property
        System.setProperty("language.file.path", dataDirFile.getAbsolutePath());
    }

    @After
    public void tearDown() throws IOException {
        // Clean up test files
        if (testFile != null && testFile.exists()) {
            testFile.delete();
        }
        if (dataDir != null) {
            Files.walk(dataDir)
                .sorted((a, b) -> b.compareTo(a))
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    public void testInitializeValidLanguage() {
        assertTrue("Should initialize with valid language code", 
                  LanguageLoader.initialize(TEST_LANG_CODE));
        assertEquals("Current language should be set", 
                    TEST_LANG_CODE, LanguageLoader.getCurrentLanguage());
    }

    @Test
    public void testInitializeInvalidLanguageCode() {
        assertFalse("Should fail with invalid language code", 
                   LanguageLoader.initialize("invalid"));
        assertEquals("Should fall back to default language", 
                    "en-CA", LanguageLoader.getCurrentLanguage());
    }

    @Test
    public void testGetTranslationExistingKey() {
        LanguageLoader.initialize(TEST_LANG_CODE);
        assertEquals("Should return correct translation", 
                    "Test Title", LanguageLoader.getTranslation("menu.title"));
    }

    @Test
    public void testGetTranslationNonExistentKey() {
        LanguageLoader.initialize(TEST_LANG_CODE);
        String nonExistentKey = "non.existent.key";
        assertEquals("Should return key itself for non-existent translation", 
                    nonExistentKey, LanguageLoader.getTranslation(nonExistentKey));
    }

    @Test
    public void testGetTranslationNullKey() {
        LanguageLoader.initialize(TEST_LANG_CODE);
        assertNull("Should return null for null key", 
                  LanguageLoader.getTranslation(null));
    }

    @Test
    public void testGetTranslationEmptyKey() {
        LanguageLoader.initialize(TEST_LANG_CODE);
        assertEquals("Should return empty string for empty key", 
                    "", LanguageLoader.getTranslation(""));
    }

    @Test
    public void testInitializeWithNullLanguageCode() {
        assertFalse("Should fail with null language code", 
                   LanguageLoader.initialize(null));
        assertEquals("Should fall back to default language", 
                    "en-CA", LanguageLoader.getCurrentLanguage());
    }

    @Test
    public void testNestedTranslationKeys() {
        LanguageLoader.initialize(TEST_LANG_CODE);
        assertEquals("Should handle nested keys", 
                    "Test Error", LanguageLoader.getTranslation("messages.error"));
    }
} 