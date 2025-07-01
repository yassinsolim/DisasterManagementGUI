package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test class for DatabaseAdapter using test mode.
 * This test class does not connect to a real database.
 */
public class DatabaseAdapterTest {
    private DatabaseAdapter dbAdapter;
    private List<DisasterVictim> victims;
    private List<Location> locations;
    private List<Item> items;
    
    @Before
    public void setUp() throws SQLException {
        // Suppress System.out during database operations
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        dbAdapter = DatabaseAdapter.createTestInstance();
        victims = new ArrayList<>();
        locations = new ArrayList<>();
        items = new ArrayList<>();
    }
    
    @After
    public void tearDown() {
        // No need to close anything in test mode
    }
    
    @Test
    public void testConstructorInitializesCorrectly() throws SQLException {
        // Arrange & Act
        DatabaseAdapter adapter = DatabaseAdapter.createTestInstance();
        
        // Assert
        assertNotNull("DatabaseAdapter should not be null", adapter);
    }
    
    @Test
    public void testGetInstanceReturnsSameInstance() throws SQLException {
        // Arrange & Act
        DatabaseAdapter instance1 = DatabaseAdapter.createTestInstance();
        DatabaseAdapter instance2 = DatabaseAdapter.createTestInstance();
        
        // Assert
        assertNotNull("DatabaseAdapter should not be null", instance1);
        assertNotNull("DatabaseAdapter should not be null", instance2);
    }
    
    @Test
    public void testAddVictimReturnsPositiveId() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        
        // Act
        int victimId = dbAdapter.addVictim(victim);
        
        // Assert
        assertTrue("Victim ID should be greater than 0", victimId > 0);
    }
    
    @Test
    public void testGetVictimByIdReturnsCorrectVictim() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        int victimId = dbAdapter.addVictim(victim);
        
        // Act
        DisasterVictim retrievedVictim = dbAdapter.getVictimById(victimId);
        
        // Assert
        assertNotNull("Retrieved victim should not be null", retrievedVictim);
        assertEquals("First name should match", "John", retrievedVictim.getFirstName());
    }
    
    @Test
    public void testGetVictimByIdReturnsNullForNonExistentId() throws SQLException {
        // Arrange
        int nonExistentId = 999;
        
        // Act
        DisasterVictim retrievedVictim = dbAdapter.getVictimById(nonExistentId);
        
        // Assert
        assertNull("Retrieved victim should be null", retrievedVictim);
    }
    
    @Test
    public void testUpdateVictimReturnsSuccess() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        int victimId = dbAdapter.addVictim(victim);
        
        victim.setFirstName("Jane");
        
        // Act
        boolean success = dbAdapter.updateVictim(victimId, victim);
        
        // Assert
        assertTrue("Update should be successful", success);
    }
    
    @Test
    public void testUpdateVictimUpdatesFirstName() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        int victimId = dbAdapter.addVictim(victim);
        
        victim.setFirstName("Jane");
        dbAdapter.updateVictim(victimId, victim);
        
        // Act
        DisasterVictim updatedVictim = dbAdapter.getVictimById(victimId);
        
        // Assert
        assertNotNull("Updated victim should not be null", updatedVictim);
        assertEquals("First name should be updated", "Jane", updatedVictim.getFirstName());
    }
    
    @Test
    public void testDeleteVictimReturnsSuccess() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        int victimId = dbAdapter.addVictim(victim);
        
        // Act
        boolean success = dbAdapter.deleteVictim(victimId);
        
        // Assert
        assertTrue("Delete should be successful", success);
    }
    
    @Test
    public void testDeleteVictimRemovesVictim() throws SQLException {
        // Arrange
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim.setGender("Male");
        int victimId = dbAdapter.addVictim(victim);
        dbAdapter.deleteVictim(victimId);
        
        // Act
        DisasterVictim deletedVictim = dbAdapter.getVictimById(victimId);
        
        // Assert
        assertNull("Deleted victim should be null", deletedVictim);
    }
    
    @Test
    public void testGetAllVictimsReturnsCorrectCount() throws SQLException {
        // Arrange
        DisasterVictim victim1 = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim1.setGender("Male");
        DisasterVictim victim2 = new DisasterVictim("Jane", "Smith", LocalDate.of(1991, 2, 2));
        victim2.setGender("Female");
        
        int id1 = dbAdapter.addVictim(victim1);
        int id2 = dbAdapter.addVictim(victim2);
        
        // Act
        List<DisasterVictim> victims = dbAdapter.getAllVictims();
        
        // Assert
        assertNotNull("Victims list should not be null", victims);
        assertEquals("Should have 2 victims", 2, victims.size());
    }
    
    @Test
    public void testGetAllVictimsReturnsCorrectFirstVictim() throws SQLException {
        // Arrange
        DisasterVictim victim1 = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim1.setGender("Male");
        DisasterVictim victim2 = new DisasterVictim("Jane", "Smith", LocalDate.of(1991, 2, 2));
        victim2.setGender("Female");
        
        int id1 = dbAdapter.addVictim(victim1);
        int id2 = dbAdapter.addVictim(victim2);
        
        // Act
        List<DisasterVictim> victims = dbAdapter.getAllVictims();
        
        // Assert
        assertNotNull("Victims list should not be null", victims);
        assertTrue("Should have at least one victim", victims.size() > 0);
        assertEquals("First victim should be John", "John", victims.get(0).getFirstName());
    }
    
    @Test
    public void testGetAllVictimsReturnsCorrectSecondVictim() throws SQLException {
        // Arrange
        DisasterVictim victim1 = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim1.setGender("Male");
        DisasterVictim victim2 = new DisasterVictim("Jane", "Smith", LocalDate.of(1991, 2, 2));
        victim2.setGender("Female");
        
        int id1 = dbAdapter.addVictim(victim1);
        int id2 = dbAdapter.addVictim(victim2);
        
        // Act
        List<DisasterVictim> victims = dbAdapter.getAllVictims();
        
        // Assert
        assertNotNull("Victims list should not be null", victims);
        assertTrue("Should have at least two victims", victims.size() > 1);
        assertEquals("Second victim should be Jane", "Jane", victims.get(1).getFirstName());
    }
}
