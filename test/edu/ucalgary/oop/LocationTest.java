package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class LocationTest {
    private Location location;
    private static final String NAME = "Test Location";
    private static final String ADDRESS = "123 Test St";

    @Before
    public void setUp() {
        location = new Location(NAME, ADDRESS);
    }

    @Test
    public void testConstructor() {
        assertEquals("Name should match", NAME, location.getName());
        assertEquals("Address should match", ADDRESS, location.getAddress());
        assertEquals("Default room should be Room A", "Room A", location.getRoom());
        assertEquals("Default grid should be Grid 1", "Grid 1", location.getGrid());
        assertTrue("Occupants list should be empty", location.getOccupants().isEmpty());
        assertTrue("Inventory should be empty", location.getInventory().isEmpty());
    }

    @Test
    public void testAddOccupant() {
        DisasterVictim victim = new DisasterVictim("John", "Doe", null);
        location.addOccupant(victim);
        assertEquals("Should have one occupant", 1, location.getOccupantCount());
        assertTrue("Should contain victim", location.getOccupants().contains(victim));
        assertEquals("Location should be set for victim", location, victim.getLocation());
    }

    @Test
    public void testRemoveOccupant() {
        DisasterVictim victim = new DisasterVictim("John", "Doe", null);
        location.addOccupant(victim);
        location.removeOccupant(victim);
        assertTrue("Occupants list should be empty", location.getOccupants().isEmpty());
        assertNull("Location should be null for victim", victim.getLocation());
    }

    @Test
    public void testMultipleOccupants() {
        DisasterVictim victim1 = new DisasterVictim("John", "Doe", null);
        DisasterVictim victim2 = new DisasterVictim("Jane", "Smith", null);
        location.addOccupant(victim1);
        location.addOccupant(victim2);
        assertEquals("Should have two occupants", 2, location.getOccupantCount());
        assertTrue("Should contain first victim", location.getOccupants().contains(victim1));
        assertTrue("Should contain second victim", location.getOccupants().contains(victim2));
    }

    @Test
    public void testAddItem() {
        Item item = new Item("Water", ItemType.WATER);
        location.addItem(item);
        assertEquals("Should have one item", 1, location.getInventoryCount());
        assertTrue("Should contain item", location.getInventory().contains(item));
    }

    @Test
    public void testRemoveItem() {
        Item item = new Item("Water", ItemType.WATER);
        location.addItem(item);
        location.removeItem(item);
        assertTrue("Inventory should be empty", location.getInventory().isEmpty());
    }

    @Test
    public void testSetRoom() {
        String newRoom = "Room B";
        location.setRoom(newRoom);
        assertEquals("Room should be updated", newRoom, location.getRoom());
    }

    @Test
    public void testSetGrid() {
        String newGrid = "Grid 2";
        location.setGrid(newGrid);
        assertEquals("Grid should be updated", newGrid, location.getGrid());
    }

    @Test
    public void testToString() {
        DisasterVictim victim = new DisasterVictim("John", "Doe", null);
        Item item = new Item("Water", ItemType.WATER);
        location.addOccupant(victim);
        location.addItem(item);
        String str = location.toString();
        assertTrue("Should contain name", str.contains(NAME));
        assertTrue("Should contain address", str.contains(ADDRESS));
        assertTrue("Should contain victim name", str.contains("John Doe"));
        assertTrue("Should contain item description", str.contains("Water"));
    }
} 