package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;

public class MedicalRecordTest {
    private MedicalRecord record;
    private static final int RECORD_ID = 1;
    private static final int PERSON_ID = 100;
    private static final String TREATMENT = "First Aid";
    private static final String LOCATION = "Hospital A";
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();

    @Before
    public void setUp() {
        record = new MedicalRecord(RECORD_ID, PERSON_ID, TREATMENT, LOCATION, DATE_TIME);
    }

    @Test
    public void testConstructor() {
        assertNotNull("Medical record should not be null", record);
        assertEquals("Record ID should match", RECORD_ID, record.getId());
        assertEquals("Person ID should match", PERSON_ID, record.getPersonId());
        assertEquals("Treatment should match", TREATMENT, record.getTreatmentDetails());
        assertEquals("Location should match", LOCATION, record.getLocation());
        assertEquals("Date and time should match", DATE_TIME, record.getDateOfTreatment());
    }

    @Test
    public void testSetTreatment() {
        String newTreatment = "Surgery";
        record.setTreatmentDetails(newTreatment);
        assertEquals("Treatment should be updated", newTreatment, record.getTreatmentDetails());
    }

    @Test
    public void testSetLocation() {
        String newLocation = "Hospital B";
        record.setLocation(newLocation);
        assertEquals("Location should be updated", newLocation, record.getLocation());
    }

    @Test
    public void testSetPersonId() {
        int newPersonId = 200;
        record.setPersonId(newPersonId);
        assertEquals("Person ID should be updated", newPersonId, record.getPersonId());
    }
} 