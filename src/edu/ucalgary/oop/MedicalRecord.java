package edu.ucalgary.oop;

import java.time.LocalDateTime;

/**
 * Represents a medical record in the disaster management system.
 * A medical record tracks treatment details for a disaster victim,
 * including when and where the treatment was administered.
 */
public class MedicalRecord {
    private int id;
    private int personId;
    private String treatmentDetails;
    private String location;
    private int locationId;
    private LocalDateTime dateOfTreatment;

    /**
     * Creates a new medical record with the specified details.
     * 
     * @param id The unique identifier for the medical record
     * @param personId The ID of the person receiving treatment
     * @param treatmentDetails Description of the treatment provided
     * @param location The name of the location where treatment was administered
     * @param dateOfTreatment The date and time when treatment was administered
     */
    public MedicalRecord(int id, int personId, String treatmentDetails, String location, LocalDateTime dateOfTreatment) {
        this.id = id;
        this.personId = personId;
        this.treatmentDetails = treatmentDetails;
        this.location = location;
        this.dateOfTreatment = dateOfTreatment;
    }

    /**
     * Gets the unique identifier of the medical record.
     * 
     * @return The medical record ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the medical record.
     * 
     * @param id The medical record ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the ID of the person who received treatment.
     * 
     * @return The person's ID
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * Sets the ID of the person who received treatment.
     * 
     * @param personId The person's ID
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * Gets the details of the treatment provided.
     * 
     * @return The treatment details
     */
    public String getTreatmentDetails() {
        return treatmentDetails;
    }

    /**
     * Sets the details of the treatment provided.
     * 
     * @param treatmentDetails The treatment details
     */
    public void setTreatmentDetails(String treatmentDetails) {
        this.treatmentDetails = treatmentDetails;
    }

    /**
     * Gets the name of the location where treatment was administered.
     * 
     * @return The location name
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the name of the location where treatment was administered.
     * 
     * @param location The location name
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the ID of the location where treatment was administered.
     * 
     * @return The location ID
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Sets the ID of the location where treatment was administered.
     * 
     * @param locationId The location ID
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Gets the date and time when treatment was administered.
     * 
     * @return The treatment date and time
     */
    public LocalDateTime getDateOfTreatment() {
        return dateOfTreatment;
    }

    /**
     * Sets the date and time when treatment was administered.
     * 
     * @param dateOfTreatment The treatment date and time
     */
    public void setDateOfTreatment(LocalDateTime dateOfTreatment) {
        this.dateOfTreatment = dateOfTreatment;
    }

    /**
     * Returns a string representation of the medical record.
     * 
     * @return A string containing the record ID, treatment details, location, and date
     */
    @Override
    public String toString() {
        return String.format("Medical Record #%d: %s at %s on %s", 
            id, treatmentDetails, location, dateOfTreatment.toLocalDate());
    }
} 