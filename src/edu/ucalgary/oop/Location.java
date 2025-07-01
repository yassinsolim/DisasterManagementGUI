package edu.ucalgary.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical location in the disaster management system.
 * A location has a unique identifier, name, address, room designation, grid reference,
 * and can contain both disaster victims (occupants) and inventory items.
 */
public class Location {
    private int locationId;
    private String name;
    private String address;
    private String room;
    private String grid;
    private List<DisasterVictim> occupants;
    private List<Item> inventory;

    /**
     * Constructs a Location with the specified name and address.
     * Initializes default values for room and grid, and empty lists for occupants and inventory.
     *
     * @param name The name of the location (cannot be null or empty)
     * @param address The address of the location (cannot be null or empty)
     * @throws IllegalArgumentException if name or address is null or empty
     */
    public Location(String name, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        this.name = name;
        this.address = address;
        this.room = "Room A";  // Default room
        this.grid = "Grid 1";  // Default grid
        this.occupants = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    /**
     * Gets the location's unique identifier.
     *
     * @return The location ID
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Gets the location's ID (alias for getLocationId).
     *
     * @return The location ID
     */
    public int getId() {
        return locationId;
    }

    /**
     * Sets the location's ID.
     *
     * @param id The ID to set
     */
    public void setId(int id) {
        this.locationId = id;
    }

    /**
     * Sets the location's ID.
     *
     * @param locationId The location ID to set
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Gets the location's name.
     *
     * @return The location name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the location's name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the location's address.
     *
     * @return The location address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the location's address.
     *
     * @param address The address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the room designation.
     *
     * @return The room designation
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room designation.
     *
     * @param room The room designation to set
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets the grid reference.
     *
     * @return The grid reference
     */
    public String getGrid() {
        return grid;
    }

    /**
     * Sets the grid reference.
     *
     * @param grid The grid reference to set
     */
    public void setGrid(String grid) {
        this.grid = grid;
    }

    /**
     * Gets a copy of the list of occupants.
     *
     * @return A new ArrayList containing the occupants
     */
    public List<DisasterVictim> getOccupants() {
        return new ArrayList<>(occupants);
    }

    /**
     * Gets the number of occupants at this location.
     *
     * @return The number of occupants
     */
    public int getOccupantCount() {
        return occupants.size();
    }

    /**
     * Adds a victim to this location and updates the victim's location reference.
     *
     * @param victim The disaster victim to add
     */
    public void addOccupant(DisasterVictim victim) {
        if (victim != null) {
            occupants.add(victim);
            victim.setLocation(this);
        }
    }

    /**
     * Removes a victim from this location and clears their location reference.
     *
     * @param victim The disaster victim to remove
     */
    public void removeOccupant(DisasterVictim victim) {
        occupants.remove(victim);
        if (victim != null) {
            victim.setLocation(null);
        }
    }

    /**
     * Gets a copy of the inventory list.
     *
     * @return A new ArrayList containing the inventory items
     */
    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    /**
     * Gets the number of items in the inventory.
     *
     * @return The number of inventory items
     */
    public int getInventoryCount() {
        return inventory.size();
    }

    /**
     * Adds an item to the location's inventory.
     *
     * @param item The item to add
     */
    public void addItem(Item item) {
        if (item != null) {
            inventory.add(item);
        }
    }

    /**
     * Removes an item from the location's inventory.
     *
     * @param item The item to remove
     */
    public void removeItem(Item item) {
        inventory.remove(item);
    }

    /**
     * Returns a string representation of the location.
     * Includes the location name and address.
     *
     * @return A formatted string describing the location
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(address).append(")");
        
        if (!occupants.isEmpty()) {
            sb.append("\nOccupants: ");
            for (DisasterVictim victim : occupants) {
                sb.append(victim.getFirstName()).append(" ").append(victim.getLastName()).append(", ");
            }
            sb.setLength(sb.length() - 2); // Remove trailing comma and space
        }
        
        if (!inventory.isEmpty()) {
            sb.append("\nInventory: ");
            for (Item item : inventory) {
                sb.append(item.getDescription()).append(", ");
            }
            sb.setLength(sb.length() - 2); // Remove trailing comma and space
        }
        
        return sb.toString();
    }

    /**
     * Checks if this location is equal to another object.
     * Locations are considered equal if they have the same name, address, room, and grid.
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Location other = (Location) obj;
        return Objects.equals(name, other.name) &&
               Objects.equals(address, other.address) &&
               Objects.equals(room, other.room) &&
               Objects.equals(grid, other.grid);
    }

    /**
     * Generates a hash code for this location.
     *
     * @return A hash code based on the location's name, address, room, and grid
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, address, room, grid);
    }
} 