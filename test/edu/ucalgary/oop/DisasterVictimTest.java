package edu.ucalgary.oop;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class DisasterVictimTest {
    private DisasterVictim victim;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.parse("1990-01-01");
    private static final String GENDER = "M";
    private static final String ENTRY_DATE = LocalDate.now().toString();

    @Before
    public void setUp() {
        victim = new DisasterVictim(FIRST_NAME, LAST_NAME, DATE_OF_BIRTH);
        victim.setGender(GENDER);
        victim.setSocialID(1);
    }

    @Test
    public void testConstructor() {
        assertEquals("First name should match", FIRST_NAME, victim.getFirstName());
        assertEquals("Last name should match", LAST_NAME, victim.getLastName());
        assertEquals("Date of birth should match", DATE_OF_BIRTH, victim.getDateOfBirth());
        assertTrue("Items list should be empty", victim.getItems().isEmpty());
        assertTrue("Medical records list should be empty", victim.getMedicalRecords().isEmpty());
    }

    @Test
    public void testSetters() {
        String newFirstName = "Jane";
        String newLastName = "Smith";
        LocalDate newDateOfBirth = LocalDate.parse("1995-05-05");
        String newGender = "F";
        String newPhoneNumber = "123-456-7890";
        int newFamilyGroupId = 2;

        victim.setFirstName(newFirstName);
        victim.setLastName(newLastName);
        victim.setDateOfBirth(newDateOfBirth);
        victim.setGender(newGender);
        victim.setPhoneNumber(newPhoneNumber);
        victim.setFamilyGroupId(newFamilyGroupId);

        assertEquals("First name should be updated", newFirstName, victim.getFirstName());
        assertEquals("Last name should be updated", newLastName, victim.getLastName());
        assertEquals("Date of birth should be updated", newDateOfBirth, victim.getDateOfBirth());
        assertEquals("Gender should be updated", newGender, victim.getGender());
        assertEquals("Phone number should be updated", newPhoneNumber, victim.getPhoneNumber());
        assertEquals("Family group ID should be updated", newFamilyGroupId, victim.getFamilyGroupId());
    }

    @Test
    public void testItemManagement() {
        Item item1 = new Item("Shirt", ItemType.CLOTHING);
        Item item2 = new Item("Apple", ItemType.FOOD);

        victim.addItem(item1);
        assertEquals("Should have one item", 1, victim.getItems().size());
        assertTrue("Should contain item1", victim.getItems().contains(item1));

        victim.addItem(item2);
        assertEquals("Should have two items", 2, victim.getItems().size());
        assertTrue("Should contain item2", victim.getItems().contains(item2));

        victim.removeItem(item1);
        assertEquals("Should have one item after removal", 1, victim.getItems().size());
        assertFalse("Should not contain item1", victim.getItems().contains(item1));
        assertTrue("Should still contain item2", victim.getItems().contains(item2));
    }

    @Test
    public void testMedicalRecordManagement() {
        MedicalRecord record1 = new MedicalRecord(1, victim.getId(), "Treatment 1", "Hospital A", LocalDateTime.now());
        MedicalRecord record2 = new MedicalRecord(2, victim.getId(), "Treatment 2", "Hospital B", LocalDateTime.now());

        victim.addMedicalRecord(record1);
        assertEquals("Should have one medical record", 1, victim.getMedicalRecords().size());
        assertTrue("Should contain record1", victim.getMedicalRecords().contains(record1));

        victim.addMedicalRecord(record2);
        assertEquals("Should have two medical records", 2, victim.getMedicalRecords().size());
        assertTrue("Should contain record2", victim.getMedicalRecords().contains(record2));

        victim.removeMedicalRecord(record1);
        assertEquals("Should have one medical record after removal", 1, victim.getMedicalRecords().size());
        assertFalse("Should not contain record1", victim.getMedicalRecords().contains(record1));
        assertTrue("Should still contain record2", victim.getMedicalRecords().contains(record2));
    }

    @Test
    public void testLocationManagement() {
        Location location = new Location("Room A", "Grid 1");
        victim.setLocation(location);
        assertEquals("Location should be set", location, victim.getLocation());
        assertEquals("Room should match", "Room A", victim.getLocation().getRoom());
        assertEquals("Grid should match", "Grid 1", victim.getLocation().getGrid());
    }

    @Test
    public void testToString() {
        String str = victim.toString();
        assertTrue("Should contain first name", str.contains(FIRST_NAME));
        assertTrue("Should contain last name", str.contains(LAST_NAME));
        assertTrue("Should contain ID", str.contains(String.valueOf(victim.getId())));
    }
} 