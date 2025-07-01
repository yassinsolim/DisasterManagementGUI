package edu.ucalgary.oop;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;

public class VictimInquiryTest {
    private VictimInquiry inquiry;
    private static final int INQUIRY_ID = 1;
    private static final int PERSON_ID = 100;
    private static final String INQUIRY_DETAILS = "Test inquiry details";
    private static final String STATUS = "Open";
    private static final String RESOLUTION = "Test resolution";
    private static final String LOCATION = "Test Location";
    private static final String COMMENTS = "Test Comments";

    @Before
    public void setUp() {
        // Arrange - Common setup
        inquiry = new VictimInquiry(PERSON_ID, INQUIRY_DETAILS);
        inquiry.setInquiryID(INQUIRY_ID);
        inquiry.setStatus(STATUS);
        inquiry.setResolution(RESOLUTION);
        inquiry.setLocation(LOCATION);
        inquiry.setComments(COMMENTS);
    }

    @Test
    public void testConstructorSetsPersonId() {
        // Assert
        assertEquals("Person ID should be set in constructor", PERSON_ID, inquiry.getPersonID());
    }

    @Test
    public void testConstructorSetsInquiryDetails() {
        // Assert
        assertEquals("Inquiry details should be set in constructor", INQUIRY_DETAILS, inquiry.getInquiryDetails());
    }

    @Test
    public void testConstructorSetsInitialStatus() {
        // Arrange
        VictimInquiry newInquiry = new VictimInquiry(PERSON_ID, INQUIRY_DETAILS);
        
        // Assert
        assertEquals("Initial status should be 'pending'", "pending", newInquiry.getStatus());
    }

    @Test
    public void testConstructorSetsCurrentDate() {
        // Assert
        assertEquals("Inquiry date should be set to current date", LocalDate.now(), inquiry.getInquiryDate());
    }

    @Test
    public void testSetInquiryIdWithValidValue() {
        // Arrange
        int newId = 2;
        
        // Act
        inquiry.setInquiryID(newId);
        
        // Assert
        assertEquals("Inquiry ID should be updated", newId, inquiry.getInquiryID());
    }

    @Test
    public void testSetStatusWithValidValue() {
        // Arrange
        String newStatus = "Resolved";
        
        // Act
        inquiry.setStatus(newStatus);
        
        // Assert
        assertEquals("Status should be updated", newStatus, inquiry.getStatus());
    }

    @Test
    public void testSetResolutionWithValidValue() {
        // Arrange
        String newResolution = "Found safe";
        
        // Act
        inquiry.setResolution(newResolution);
        
        // Assert
        assertEquals("Resolution should be updated", newResolution, inquiry.getResolution());
    }

    @Test
    public void testSetLocationWithValidValue() {
        // Arrange
        String newLocation = "New Location";
        
        // Act
        inquiry.setLocation(newLocation);
        
        // Assert
        assertEquals("Location should be updated", newLocation, inquiry.getLocation());
    }

    @Test
    public void testSetCommentsWithValidValue() {
        // Arrange
        String newComments = "New Comments";
        
        // Act
        inquiry.setComments(newComments);
        
        // Assert
        assertEquals("Comments should be updated", newComments, inquiry.getComments());
    }

    @Test
    public void testIsValidPersonIDWithPositiveID() {
        // Assert
        assertTrue("Valid person ID should return true", inquiry.isValidPersonID());
    }

    @Test
    public void testIsValidInquiryDetailsWithValidDetails() {
        // Assert
        assertTrue("Valid inquiry details should return true", inquiry.isValidInquiryDetails());
    }

    @Test
    public void testToStringContainsAllFields() {
        // Act
        String str = inquiry.toString();
        
        // Assert
        assertTrue("Should contain inquiry ID", str.contains(String.valueOf(INQUIRY_ID)));
        assertTrue("Should contain person ID", str.contains(String.valueOf(PERSON_ID)));
        assertTrue("Should contain inquiry details", str.contains(INQUIRY_DETAILS));
        assertTrue("Should contain status", str.contains(STATUS));
        assertTrue("Should contain resolution", str.contains(RESOLUTION));
    }
} 