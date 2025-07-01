/**
 * Represents a victim inquiry in the disaster management system.
 * This class manages inquiries about victims, including their status and location.
 * It tracks the progress of inquiries from creation to resolution.
 *
 * @author University of Calgary OOP Team
 * @version 1.0
 * @since 2024-03-20
 */
package edu.ucalgary.oop;

import java.time.LocalDate;

public class VictimInquiry {
    private int inquiryID;
    private int personID;
    private LocalDate inquiryDate;
    private String inquiryDetails;
    private String status;
    private String resolution;
    private String location;
    private String comments;

    /**
     * Constructs a new VictimInquiry with the specified details.
     * Sets the inquiry date to the current date and initial status to "pending".
     *
     * @param personID The ID of the person being inquired about
     * @param inquiryDetails The details of the inquiry
     */
    public VictimInquiry(int personID, String inquiryDetails) {
        this.personID = personID;
        this.inquiryDetails = inquiryDetails;
        this.inquiryDate = LocalDate.now();
        this.status = "pending";
    }

    /**
     * Gets the unique identifier for this inquiry.
     *
     * @return The inquiry ID
     */
    public int getInquiryID() { return inquiryID; }

    /**
     * Gets the ID of the person this inquiry is about.
     *
     * @return The person ID
     */
    public int getPersonID() { return personID; }

    /**
     * Gets the date when this inquiry was created.
     *
     * @return The date of the inquiry
     */
    public LocalDate getInquiryDate() { return inquiryDate; }

    /**
     * Gets the details of this inquiry.
     *
     * @return The inquiry details
     */
    public String getInquiryDetails() { return inquiryDetails; }

    /**
     * Gets the current status of this inquiry.
     *
     * @return The inquiry status
     */
    public String getStatus() { return status; }

    /**
     * Gets the resolution of this inquiry, if any.
     *
     * @return The inquiry resolution, or null if not resolved
     */
    public String getResolution() { return resolution; }

    /**
     * Gets the location associated with this inquiry.
     *
     * @return The location, or null if not specified
     */
    public String getLocation() { return location; }

    /**
     * Gets any additional comments about this inquiry.
     *
     * @return The comments, or null if none
     */
    public String getComments() { return comments; }

    /**
     * Sets the unique identifier for this inquiry.
     *
     * @param inquiryID The new inquiry ID
     */
    public void setInquiryID(int id) { this.inquiryID = id; }

    /**
     * Sets the status of this inquiry.
     *
     * @param status The new status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Sets the resolution of this inquiry.
     *
     * @param resolution The resolution details
     */
    public void setResolution(String resolution) { this.resolution = resolution; }

    /**
     * Sets the location associated with this inquiry.
     *
     * @param location The location
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Sets additional comments about this inquiry.
     *
     * @param comments The comments
     */
    public void setComments(String comments) { this.comments = comments; }

    // Validation methods
    public boolean isValidPersonID() {
        return personID > 0;
    }

    public boolean isValidInquiryDetails() {
        return inquiryDetails != null && !inquiryDetails.trim().isEmpty();
    }

    // Convert to string representation
    @Override
    public String toString() {
        return String.format("Victim Inquiry #%d\nPerson ID: %d\nDate: %s\nDetails: %s\nStatus: %s\nResolution: %s",
            inquiryID, personID, inquiryDate, inquiryDetails, status, 
            resolution != null ? resolution : "Not resolved");
    }
} 