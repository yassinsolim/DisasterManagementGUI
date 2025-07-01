package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import edu.ucalgary.oop.Item;
import edu.ucalgary.oop.ItemType;
import edu.ucalgary.oop.DisasterVictim;
import edu.ucalgary.oop.Location;

public class ItemTest {
    private Item item;
    private static final int ITEM_ID = 1;
    private static final ItemType ITEM_TYPE = ItemType.WATER;
    private static final String DESCRIPTION = "Bottled water";
    private static final int QUANTITY = 10;
    private static final String DATE = "20240315";
    private static final String ROOM = "Room A";
    private static final String GRID = "Grid 1";

    @Before
    public void setUp() {
        item = new Item(ITEM_ID, ITEM_TYPE, DESCRIPTION, QUANTITY, DATE, ROOM, GRID);
    }

    @Test
    public void testConstructorSetsItemId() {
        assertEquals("Item ID should be set in constructor", ITEM_ID, item.getItemID());
    }

    @Test
    public void testConstructorSetsItemType() {
        assertEquals("Item type should be set in constructor", ITEM_TYPE, item.getItemType());
    }

    @Test
    public void testConstructorSetsDescription() {
        assertEquals("Description should be set in constructor", DESCRIPTION, item.getDescription());
    }

    @Test
    public void testConstructorSetsQuantity() {
        assertEquals("Quantity should be set in constructor", QUANTITY, item.getQuantity());
    }

    @Test
    public void testConstructorSetsDate() {
        assertEquals("Date should be set in constructor", DATE, item.getDate());
    }

    @Test
    public void testConstructorSetsRoom() {
        assertEquals("Room should be set in constructor", ROOM, item.getRoom());
    }

    @Test
    public void testConstructorSetsGrid() {
        assertEquals("Grid should be set in constructor", GRID, item.getGrid());
    }

    @Test
    public void testSetItemIdWithValidValue() {
        int newId = 2;
        item.setItemID(newId);
        assertEquals("Item ID should be updated", newId, item.getItemID());
    }

    @Test
    public void testSetItemTypeWithValidType() {
        ItemType newType = ItemType.BLANKET;
        item.setItemType(newType);
        assertEquals("Item type should be updated", newType, item.getItemType());
    }

    @Test
    public void testSetDescriptionWithValidInput() {
        String newDescription = "Emergency blanket";
        item.setDescription(newDescription);
        assertEquals("Description should be updated", newDescription, item.getDescription());
    }

    @Test
    public void testSetQuantityWithValidValue() {
        int newQuantity = 5;
        item.setQuantity(newQuantity);
        assertEquals("Quantity should be updated", newQuantity, item.getQuantity());
    }

    @Test
    public void testSetDateWithValidFormat() {
        String newDate = "20240501";
        item.setDate(newDate);
        assertEquals("Date should be updated", newDate, item.getDate());
    }

    @Test
    public void testSetRoomWithValidInput() {
        String newRoom = "Room B";
        item.setRoom(newRoom);
        assertEquals("Room should be updated", newRoom, item.getRoom());
    }

    @Test
    public void testSetGridWithValidInput() {
        String newGrid = "Grid 2";
        item.setGrid(newGrid);
        assertEquals("Grid should be updated", newGrid, item.getGrid());
    }

    @Test
    public void testAllocateToValidVictim() {
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.now());
        Location location = new Location(ROOM, GRID);
        victim.setLocation(location);
        item.allocateTo(victim);
        assertEquals("Item should be allocated to victim", victim, item.getAllocatedTo());
    }

    @Test
    public void testAllocateToLocationClearsVictimAllocation() {
        Location location = new Location("Room B", "Grid 2");
        item.allocateToLocation(location);
        assertNull("Item should not be allocated to victim after location allocation", item.getAllocatedTo());
    }

    @Test
    public void testWaterItemExpirationWithPastDate() {
        String pastDate = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Item waterItem = new Item(1, ItemType.WATER, "Water", 1, pastDate, ROOM, GRID);
        assertTrue("Water item with past date should be expired", waterItem.isExpired());
    }

    @Test
    public void testNonWaterItemNeverExpires() {
        String pastDate = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Item nonWaterItem = new Item(1, ItemType.BLANKET, "Blanket", 1, pastDate, ROOM, GRID);
        assertFalse("Non-water item should never be expired", nonWaterItem.isExpired());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocatePersonalBelongingToNullVictim() {
        Item personalItem = new Item("Wallet", ItemType.PERSONAL_BELONGING);
        personalItem.allocateTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocatePersonalBelongingToLocation() {
        Item personalItem = new Item("Wallet", ItemType.PERSONAL_BELONGING);
        Location location = new Location("Room A", "Grid 1");
        personalItem.allocateToLocation(location);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocateToVictimInDifferentLocation() {
        Item item = new Item("Water", ItemType.WATER);
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.now());
        Location victimLocation = new Location("Room A", "Grid 1");
        victim.setLocation(victimLocation);
        item.setRoom("Room B");
        item.setGrid("Grid 2");
        item.allocateTo(victim);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAllocateToLocationAfterVictim() {
        Item item = new Item("Water", ItemType.WATER);
        DisasterVictim victim = new DisasterVictim("John", "Doe", LocalDate.now());
        Location location = new Location("Room A", "Grid 1");
        victim.setLocation(location);
        item.allocateTo(victim);
        item.allocateToLocation(location);
    }
} 