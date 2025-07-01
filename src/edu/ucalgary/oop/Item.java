package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Represents an item in the disaster management system.
 * This class stores information about items such as water, cots, blankets, and personal belongings.
 */
public class Item {
    private int itemID;
    private ItemType itemType;
    private String description;
    private int quantity;
    private String date;
    private String room;
    private String grid;
    private DisasterVictim allocatedTo;
    private Location allocatedToLocation;
    private static int counter = 1001;

    /**
     * Constructs a new Item with the specified details.
     * 
     * @param itemID The unique identifier for the item
     * @param itemType The type of the item (WATER, COT, BLANKET, PERSONAL_BELONGING)
     * @param description A description of the item
     * @param quantity The quantity of the item
     * @param date The expiry date of the item in YYYYMMDD format, or null if not applicable
     * @param room The room where the item is stored
     * @param grid The grid location within the room
     */
    public Item(int itemID, ItemType itemType, String description, int quantity, String date, String room, String grid) {
        this.itemID = itemID;
        this.itemType = itemType;
        this.description = description;
        this.quantity = quantity;
        this.date = date;
        this.room = room;
        this.grid = grid;
    }

    /**
     * Simplified constructor for testing purposes.
     * 
     * @param description A description of the item
     * @param itemType The type of the item
     */
    public Item(String description, ItemType itemType) {
        this.itemID = counter++;
        this.itemType = itemType;
        this.description = description;
        this.quantity = 1;
        this.date = null;
        this.room = null;
        this.grid = null;
    }

    /**
     * Gets the unique identifier of the item.
     * 
     * @return The item ID
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * Sets the unique identifier of the item.
     * 
     * @param id The new item ID
     */
    public void setItemID(int id) {
        this.itemID = id;
    }

    /**
     * Gets the type of the item.
     * 
     * @return The item type
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * Sets the type of the item.
     * 
     * @param type The new item type
     */
    public void setItemType(ItemType type) {
        this.itemType = type;
    }

    /**
     * Gets the description of the item.
     * 
     * @return The item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the item.
     * 
     * @param desc The new item description
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Gets the quantity of the item.
     * 
     * @return The item quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item.
     * 
     * @param quantity The new item quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the expiry date of the item.
     * 
     * @return The expiry date in YYYYMMDD format, or null if not applicable
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the expiry date of the item.
     * 
     * @param date The expiry date in YYYYMMDD format
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the room where the item is stored.
     * 
     * @return The room designation
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room where the item is stored.
     * 
     * @param room The room designation
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets the grid location within the room.
     * 
     * @return The grid reference
     */
    public String getGrid() {
        return grid;
    }

    /**
     * Sets the grid location within the room.
     * 
     * @param grid The grid reference
     */
    public void setGrid(String grid) {
        this.grid = grid;
    }

    /**
     * Gets the disaster victim this item is allocated to.
     * 
     * @return The allocated disaster victim, or null if not allocated to a person
     */
    public DisasterVictim getAllocatedTo() {
        return allocatedTo;
    }

    /**
     * Allocates this item to a disaster victim.
     * Personal belongings must be allocated to a person.
     * An item cannot be allocated to both a person and a location.
     * The person must be at the same location as the item.
     * 
     * @param person The disaster victim to allocate the item to
     * @throws IllegalArgumentException if the allocation is invalid
     */
    public void allocateTo(DisasterVictim person) {
        if (itemType == ItemType.PERSONAL_BELONGING && person == null) {
            throw new IllegalArgumentException("Personal belongings must be allocated to a person");
        }
        
        if (person != null && allocatedToLocation != null) {
            throw new IllegalArgumentException("Item is already allocated to a location");
        }

        if (person != null && person.getLocation() != null) {
            Location personLocation = person.getLocation();
            if (!personLocation.getRoom().equals(room) || !personLocation.getGrid().equals(grid)) {
                throw new IllegalArgumentException("Person must be at the same location as the item");
            }
        }

        this.allocatedTo = person;
        this.allocatedToLocation = null;
    }

    /**
     * Gets the location this item is allocated to.
     * 
     * @return The allocated location, or null if not allocated to a location
     */
    public Location getAllocatedToLocation() {
        return allocatedToLocation;
    }

    /**
     * Allocates this item to a location.
     * Personal belongings cannot be allocated to locations.
     * An item cannot be allocated to both a person and a location.
     * 
     * @param location The location to allocate the item to
     * @throws IllegalArgumentException if the allocation is invalid
     */
    public void allocateToLocation(Location location) {
        if (itemType == ItemType.PERSONAL_BELONGING) {
            throw new IllegalArgumentException("Personal belongings cannot be allocated to locations");
        }
        
        if (allocatedTo != null) {
            throw new IllegalArgumentException("Item is already allocated to a person");
        }

        this.allocatedToLocation = location;
        this.allocatedTo = null;
    }

    /**
     * Checks if the item is expired.
     * Only water items can expire, based on their expiry date.
     * 
     * @return true if the item is expired, false otherwise
     */
    public boolean isExpired() {
        if (itemType != ItemType.WATER || date == null) {
            return false;
        }

        try {
            LocalDate expiryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate today = LocalDate.now();
            return today.isAfter(expiryDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns a string representation of the item.
     * 
     * @return A string containing the item's type, description, and ID
     */
    @Override
    public String toString() {
        return "Item{" +
               "itemID=" + itemID +
               ", itemType=" + itemType +
               ", description='" + description + '\'' +
               ", quantity=" + quantity +
               ", date='" + date + '\'' +
               ", room='" + room + '\'' +
               ", grid='" + grid + '\'' +
               ", allocatedTo=" + (allocatedTo != null ? allocatedTo.getFirstName() + " " + allocatedTo.getLastName() : "null") +
               ", allocatedToLocation=" + (allocatedToLocation != null ? allocatedToLocation.toString() : "null") +
               '}';
    }

    /**
     * Checks if this item is equal to another object.
     * Items are considered equal if they have the same ID, type, description,
     * quantity, date, room, and grid.
     * 
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item other = (Item) obj;
        return itemID == other.itemID &&
               itemType == other.itemType &&
               quantity == other.quantity &&
               Objects.equals(description, other.description) &&
               Objects.equals(date, other.date) &&
               Objects.equals(room, other.room) &&
               Objects.equals(grid, other.grid) &&
               Objects.equals(allocatedTo, other.allocatedTo) &&
               Objects.equals(allocatedToLocation, other.allocatedToLocation);
    }

    /**
     * Generates a hash code for this item.
     * The hash code is based on the item's ID, type, description, quantity,
     * date, room, and grid.
     * 
     * @return A hash code for this item
     */
    @Override
    public int hashCode() {
        return Objects.hash(itemID, itemType, description, quantity, date, room, grid, allocatedTo, allocatedToLocation);
    }
} 