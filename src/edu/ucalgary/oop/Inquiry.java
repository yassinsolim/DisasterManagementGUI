package edu.ucalgary.oop;

import java.time.LocalDate;

/**
 * Represents an inquiry in the disaster management system.
 * An inquiry tracks requests for information about disaster victims,
 * including who is making the inquiry, who they are seeking,
 * and where the inquiry is being made.
 */
public class Inquiry {
    private int id;
    private Integer inquirerId;
    private Integer seekingId;
    private Integer locationId;
    private LocalDate dateOfInquiry;
    private String comments;

    /**
     * Creates a new empty inquiry.
     */
    public Inquiry() {
    }

    /**
     * Creates a new inquiry with the specified comments and date.
     * 
     * @param comments The inquiry comments or details
     * @param dateOfInquiry The date when the inquiry was made
     */
    public Inquiry(String comments, LocalDate dateOfInquiry) {
        this.comments = comments;
        this.dateOfInquiry = dateOfInquiry;
    }

    /**
     * Gets the unique identifier of the inquiry.
     * 
     * @return The inquiry ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the inquiry.
     * 
     * @param id The inquiry ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the disaster victim making the inquiry.
     * 
     * @return The inquirer's ID, or null if not set
     */
    public Integer getInquirerId() {
        return inquirerId;
    }

    /**
     * Sets the ID of the disaster victim making the inquiry.
     * 
     * @param inquirerId The inquirer's ID
     */
    public void setInquirerId(Integer inquirerId) {
        this.inquirerId = inquirerId;
    }

    /**
     * Gets the ID of the disaster victim being sought.
     * 
     * @return The sought victim's ID, or null if not set
     */
    public Integer getSeekingId() {
        return seekingId;
    }

    /**
     * Sets the ID of the disaster victim being sought.
     * 
     * @param seekingId The sought victim's ID
     */
    public void setSeekingId(Integer seekingId) {
        this.seekingId = seekingId;
    }

    /**
     * Gets the ID of the location where the inquiry was made.
     * 
     * @return The location ID, or null if not set
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the ID of the location where the inquiry was made.
     * 
     * @param locationId The location ID
     */
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    /**
     * Gets the date when the inquiry was made.
     * 
     * @return The inquiry date
     */
    public LocalDate getDateOfInquiry() {
        return dateOfInquiry;
    }

    /**
     * Sets the date when the inquiry was made.
     * 
     * @param dateOfInquiry The inquiry date
     */
    public void setDateOfInquiry(LocalDate dateOfInquiry) {
        this.dateOfInquiry = dateOfInquiry;
    }

    /**
     * Gets the comments or details of the inquiry.
     * 
     * @return The inquiry comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments or details of the inquiry.
     * 
     * @param comments The inquiry comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Returns a string representation of the inquiry.
     * 
     * @return A string containing the inquiry ID and comments
     */
    @Override
    public String toString() {
        return String.format("Inquiry #%d: %s", id, comments);
    }
} 