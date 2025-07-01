package edu.ucalgary.oop;

/**
 * Represents a supply item in the disaster management system.
 * Each supply has a unique identifier, type, and optional comments.
 */
public class Supply {
    private int id;
    private String type;
    private String comments;

    /**
     * Constructs a Supply with a specified ID, type, and comments.
     *
     * @param id The unique identifier for the supply
     * @param type The type of supply (cannot be null or empty)
     * @param comments Additional comments or notes about the supply
     * @throws IllegalArgumentException if type is null or empty
     */
    public Supply(int id, String type, String comments) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty");
        }
        this.id = id;
        this.type = type;
        this.comments = comments;
    }

    /**
     * Constructs a Supply with a type and comments. The ID will be set later.
     *
     * @param type The type of supply (cannot be null or empty)
     * @param comments Additional comments or notes about the supply
     * @throws IllegalArgumentException if type is null or empty
     */
    public Supply(String type, String comments) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty");
        }
        this.type = type;
        this.comments = comments;
    }

    /**
     * Gets the supply's unique identifier.
     *
     * @return The supply's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the supply's unique identifier.
     *
     * @param id The ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the supply's type.
     *
     * @return The supply's type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the supply's type.
     *
     * @param type The type to set (cannot be null or empty)
     * @throws IllegalArgumentException if type is null or empty
     */
    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Supply type cannot be null or empty");
        }
        this.type = type;
    }

    /**
     * Gets the comments associated with the supply.
     *
     * @return The supply's comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments associated with the supply.
     *
     * @param comments The comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns a string representation of the supply.
     *
     * @return A string in the format "type (ID: id)"
     */
    @Override
    public String toString() {
        return String.format("%s (ID: %d)", type, id);
    }
} 