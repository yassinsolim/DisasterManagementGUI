/**
 * Manages language loading and translation functionality for the Disaster Management System.
 * This class handles loading language files from various locations and provides translation
 * services for the application's user interface.
 *
 * @author University of Calgary OOP Team
 * @version 1.0
 * @since 2024-03-20
 */
package edu.ucalgary.oop;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.util.regex.Pattern;

public class LanguageLoader {
    private static Map<String, String> translations;
    private static String currentLanguage;
    private static final Pattern LANG_CODE_PATTERN = Pattern.compile("^[a-z]{2}-[A-Z]{2}$");
    private static final String DEFAULT_LANG = "en-CA";

    /**
     * Initialize the language loader with the specified language code.
     * Attempts to load the language file and falls back to default language if unsuccessful.
     * 
     * @param languageCode The language code in the format "aa-BB" (e.g., "en-CA")
     * @return true if the language was loaded successfully, false otherwise
     */
    public static boolean initialize(String languageCode) {
        if (languageCode == null || !LANG_CODE_PATTERN.matcher(languageCode).matches()) {
            ErrorLogger.logError("Invalid language code format: " + languageCode);
            currentLanguage = DEFAULT_LANG;
            loadDefaultTranslations();
            return false;
        }

        translations = new HashMap<>();
        currentLanguage = languageCode;
        
        boolean success = loadLanguage(languageCode);
        if (!success) {
            translations.clear();
            currentLanguage = DEFAULT_LANG;
            // Load default translations as fallback
            loadDefaultTranslations();
            return false;
        }
        
        return true;
    }

    /**
     * Loads a language file from the specified path.
     * Attempts to load from multiple locations in order:
     * 1. Custom path specified by system property
     * 2. Data directory
     * 3. Classpath resources
     *
     * @param languageCode The language code to load
     * @return true if the language was loaded successfully, false otherwise
     */
    private static boolean loadLanguage(String languageCode) {
        try {
            System.out.println("Attempting to load language file: " + languageCode);
            // Try to load from the system property path first
            String customPath = System.getProperty("language.file.path");
            if (customPath != null) {
                File langFile = new File(customPath, languageCode + ".xml");
                System.out.println("Checking custom path: " + langFile.getAbsolutePath());
                if (langFile.exists()) {
                    InputStream inputStream = new FileInputStream(langFile);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new InputSource(inputStream));
                    doc.getDocumentElement().normalize();
                    processElement(doc.getDocumentElement(), "");
                    return true;
                }
            }

            // Try to load from the data directory
            File dataDir = new File("data");
            if (dataDir.exists() && dataDir.isDirectory()) {
                File langFile = new File(dataDir, languageCode + ".xml");
                System.out.println("Checking data directory: " + langFile.getAbsolutePath());
                if (langFile.exists()) {
                    System.out.println("Found language file in data directory");
                    InputStream inputStream = new FileInputStream(langFile);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new InputSource(inputStream));
                    doc.getDocumentElement().normalize();
                    processElement(doc.getDocumentElement(), "");
                    System.out.println("Successfully loaded translations. Available keys: " + translations.keySet());
                    return true;
                }
            }
            
            // Fall back to classpath resources
            String resourcePath = "/languages/" + languageCode + ".xml";
            System.out.println("Checking classpath: " + resourcePath);
            InputStream inputStream = LanguageLoader.class.getResourceAsStream(resourcePath);
            if (inputStream != null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(inputStream));
                doc.getDocumentElement().normalize();
                processElement(doc.getDocumentElement(), "");
                return true;
            }

            // If no file found, return false
            System.err.println("Language file not found: " + languageCode);
            return false;
        } catch (Exception e) {
            System.err.println("Failed to load language file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the default language translations.
     * This is called when the specified language cannot be loaded.
     */
    private static void loadDefaultTranslations() {
        translations = new HashMap<>();
        currentLanguage = DEFAULT_LANG;
        
        translations.put("menu.title", "Disaster Victim Management System");
        translations.put("menu.victims", "Manage Victims");
        translations.put("menu.supplies", "Manage Items");
        translations.put("menu.inquiries", "Manage Inquiries");
        translations.put("menu.locations", "Manage Locations");
        translations.put("menu.exit", "Exit");
        translations.put("menu.victim", "Victim Management");
        translations.put("menu.add_victim", "Add Victim");
        translations.put("menu.update_victim", "Update Victim");
        translations.put("menu.delete_victim", "Delete Victim");
        translations.put("menu.view_victim", "View Victim");
        translations.put("menu.back", "Back to Main Menu");
        translations.put("menu.item", "Item Management");
        translations.put("menu.add_item", "Add Item");
        translations.put("menu.update_item", "Update Item");
        translations.put("menu.delete_item", "Delete Item");
        translations.put("menu.view_item", "View Item");
        translations.put("menu.location", "Location Management");
        translations.put("menu.add_location", "Add Location");
        translations.put("menu.update_location", "Update Location");
        translations.put("menu.delete_location", "Delete Location");
        translations.put("menu.view_location", "View Location");
        translations.put("error.invalid_language", "Invalid language code");
        translations.put("error.file_not_found", "Language file not found");
        translations.put("error.loading_failed", "Failed to load language file");
        translations.put("messages.invalid_choice", "Invalid choice");
        translations.put("messages.goodbye", "Goodbye");
        translations.put("prompt.choice", "Enter your choice (1-5): ");
    }

    /**
     * Processes an XML element and its children to extract translations.
     * Recursively processes nested elements to build the translation map.
     *
     * @param element The XML element to process
     * @param prefix The prefix for the current element's path
     */
    private static void processElement(Element element, String prefix) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;
                String key = prefix.isEmpty() ? child.getTagName() : prefix + "." + child.getTagName();
                
                // If this element has only text content or no children, add it as a translation
                if (!child.hasChildNodes() || 
                    (child.getChildNodes().getLength() == 1 && 
                     child.getFirstChild().getNodeType() == Node.TEXT_NODE)) {
                    String value = child.getTextContent().trim();
                    System.out.println("Adding translation: " + key + " = " + value);
                    translations.put(key, value);
                } else {
                    // Otherwise, recursively process its children
                    processElement(child, key);
                }
            }
        }
    }

    /**
     * Gets a translation for the specified key.
     * 
     * @param key The translation key to look up
     * @return The translation if found, the key itself if not found, null if key is null,
     *         or empty string if key is empty
     */
    public static String getTranslation(String key) {
        if (key == null) {
            return null;
        }
        if (key.isEmpty()) {
            return "";
        }
        return translations.getOrDefault(key, key);
    }

    /**
     * Gets the current language code.
     * 
     * @return The current language code
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
} 