package edu.ucalgary.oop;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a disaster victim in the Disaster Management System.
 * This class maintains information about a victim's personal details, medical records,
 * assigned items, location, and family group associations.
 *
 * <p>The class provides functionality to:
 * <ul>
 *   <li>Manage personal information (name, date of birth, gender, etc.)</li>
 *   <li>Track medical records and treatments</li>
 *   <li>Manage assigned items and supplies</li>
 *   <li>Handle location assignments</li>
 *   <li>Manage family group associations</li>
 * </ul>
 *
 * @author Yassin Soliman
 * @version 1.0
 * @since 2025-04-13
 *
 * @see Item
 * @see MedicalRecord
 * @see Location
 * @see FamilyGroup
 */
public class DisasterVictim {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String entryDate;
    private List<Item> items;
    private List<MedicalRecord> medicalRecords;
    private FamilyGroup familyGroup;
    private int socialID;
    private String comments;
    private Location location;
    private String phoneNumber;
    private int familyGroupId;

    /**
     * Creates a new DisasterVictim with the specified personal information.
     * Initializes empty lists for items and medical records, and sets the entry date to the current date.
     *
     * @param firstName The victim's first name
     * @param lastName The victim's last name
     * @param dateOfBirth The victim's date of birth
     */
    public DisasterVictim(String firstName, String lastName, LocalDate dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.items = new ArrayList<>();
        this.medicalRecords = new ArrayList<>();
        this.entryDate = LocalDate.now().toString();
    }

    // Getters and setters with documentation
    /**
     * Gets the victim's first name.
     *
     * @return The first name
     */
    public String getFirstName() { return firstName; }

    /**
     * Sets the victim's first name.
     *
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Gets the victim's last name.
     *
     * @return The last name
     */
    public String getLastName() { return lastName; }

    /**
     * Sets the victim's last name.
     *
     * @param lastName The last name to set
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Gets the victim's date of birth.
     *
     * @return The date of birth
     */
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    /**
     * Sets the victim's date of birth.
     *
     * @param dateOfBirth The date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    /**
     * Gets the victim's gender.
     *
     * @return The gender
     */
    public String getGender() { return gender; }

    /**
     * Sets the victim's gender.
     *
     * @param gender The gender to set
     */
    public void setGender(String gender) { this.gender = gender; }

    /**
     * Gets the victim's entry date into the system.
     *
     * @return The entry date as a string
     */
    public String getEntryDate() { return entryDate; }

    /**
     * Sets the victim's entry date into the system.
     *
     * @param entryDate The entry date to set
     */
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    /**
     * Gets the victim's family group.
     *
     * @return The family group
     */
    public FamilyGroup getFamilyGroup() { return familyGroup; }

    /**
     * Sets the victim's family group.
     *
     * @param familyGroup The family group to set
     */
    public void setFamilyGroup(FamilyGroup familyGroup) { this.familyGroup = familyGroup; }

    /**
     * Gets the victim's social ID number.
     *
     * @return The social ID
     */
    public int getSocialID() { return socialID; }

    /**
     * Gets the victim's ID (alias for getSocialID).
     *
     * @return The victim's ID
     */
    public int getId() { return socialID; }

    /**
     * Sets the victim's ID.
     *
     * @param id The ID to set
     */
    public void setId(int id) { this.socialID = id; }

    /**
     * Sets the victim's social ID.
     *
     * @param socialID The social ID to set
     */
    public void setSocialID(int socialID) { this.socialID = socialID; }

    /**
     * Gets any comments associated with the victim.
     *
     * @return The comments
     */
    public String getComments() { return comments; }

    /**
     * Sets comments for the victim.
     *
     * @param comments The comments to set
     */
    public void setComments(String comments) { this.comments = comments; }

    /**
     * Gets the victim's assigned location.
     *
     * @return The location
     */
    public Location getLocation() { return location; }

    /**
     * Sets the victim's assigned location.
     *
     * @param location The location to set
     */
    public void setLocation(Location location) { this.location = location; }

    /**
     * Gets the victim's phone number.
     *
     * @return The phone number
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Sets the victim's phone number.
     *
     * @param phoneNumber The phone number to set
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * Gets the victim's family group ID.
     *
     * @return The family group ID
     */
    public int getFamilyGroupId() { return familyGroupId; }

    /**
     * Sets the victim's family group ID.
     *
     * @param familyGroupId The family group ID to set
     */
    public void setFamilyGroupId(int familyGroupId) { this.familyGroupId = familyGroupId; }

    // Item management methods
    /**
     * Gets a copy of the list of items assigned to the victim.
     *
     * @return A new ArrayList containing the victim's items
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Adds an item to the victim's assigned items.
     *
     * @param item The item to add
     */
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Removes an item from the victim's assigned items.
     *
     * @param item The item to remove
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    // Medical record management methods
    /**
     * Gets a copy of the list of medical records for the victim.
     *
     * @return A new ArrayList containing the victim's medical records
     */
    public List<MedicalRecord> getMedicalRecords() {
        return new ArrayList<>(medicalRecords);
    }

    /**
     * Adds a medical record to the victim's records.
     *
     * @param record The medical record to add
     */
    public void addMedicalRecord(MedicalRecord record) {
        if (record != null) {
            medicalRecords.add(record);
        }
    }

    /**
     * Removes a medical record from the victim's records.
     *
     * @param record The medical record to remove
     */
    public void removeMedicalRecord(MedicalRecord record) {
        medicalRecords.remove(record);
    }

    /**
     * Checks if this victim is equal to another object.
     * Victims are considered equal if they have the same first name, last name,
     * date of birth, gender, entry date, items, medical records, and family group.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisasterVictim that = (DisasterVictim) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(entryDate, that.entryDate) &&
                Objects.equals(items, that.items) &&
                Objects.equals(medicalRecords, that.medicalRecords) &&
                Objects.equals(familyGroup, that.familyGroup);
    }

    /**
     * Generates a hash code for this victim.
     * The hash code is based on the victim's first name, last name, date of birth,
     * gender, entry date, items, medical records, and family group.
     *
     * @return A hash code for this victim
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dateOfBirth, gender, entryDate, items, medicalRecords, familyGroup);
    }

    /**
     * Returns a string representation of this victim.
     * The format is "firstName lastName (ID: socialID)".
     *
     * @return A string representation of this victim
     */
    @Override
    public String toString() {
        return String.format("%s %s (ID: %d)", firstName, lastName, socialID);
    }
}