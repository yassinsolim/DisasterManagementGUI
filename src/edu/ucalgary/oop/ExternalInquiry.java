package edu.ucalgary.oop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExternalInquiry {
    private int inquiryID;
    private String inquirerName;
    private String inquirerContact;
    private LocalDate inquiryDate;
    private String inquiryDetails;
    private String status;
    private String resolution;

    public ExternalInquiry(String inquirerName, String inquirerContact, String inquiryDetails) {
        this.inquirerName = inquirerName;
        this.inquirerContact = inquirerContact;
        this.inquiryDetails = inquiryDetails;
        this.inquiryDate = LocalDate.now();
        this.status = "pending";
    }

    // Getters
    public int getInquiryID() { return inquiryID; }
    public String getInquirerName() { return inquirerName; }
    public String getInquirerContact() { return inquirerContact; }
    public LocalDate getInquiryDate() { return inquiryDate; }
    public String getInquiryDetails() { return inquiryDetails; }
    public String getStatus() { return status; }
    public String getResolution() { return resolution; }

    // Setters
    public void setInquiryID(int id) { this.inquiryID = id; }
    public void setStatus(String status) { this.status = status; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    // Validation methods
    public boolean isValidInquirerName() {
        return inquirerName != null && !inquirerName.trim().isEmpty();
    }

    public boolean isValidInquirerContact() {
        return inquirerContact != null && !inquirerContact.trim().isEmpty();
    }

    public boolean isValidInquiryDetails() {
        return inquiryDetails != null && !inquiryDetails.trim().isEmpty();
    }

    // Convert to string representation
    @Override
    public String toString() {
        return String.format("External Inquiry #%d\nInquirer: %s\nContact: %s\nDate: %s\nDetails: %s\nStatus: %s\nResolution: %s",
            inquiryID, inquirerName, inquirerContact, inquiryDate, inquiryDetails, status, 
            resolution != null ? resolution : "Not resolved");
    }
} 