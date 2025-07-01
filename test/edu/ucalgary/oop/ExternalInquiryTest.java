package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;

public class ExternalInquiryTest {
    private ExternalInquiry inquiry;
    private static final int INQUIRY_ID = 1;
    private static final String INQUIRER_NAME = "John Doe";
    private static final String INQUIRER_CONTACT = "test@example.com";
    private static final String INQUIRY_DETAILS = "Test inquiry details";
    private static final String STATUS = "Open";
    private static final String RESOLUTION = "Test resolution";

    @Before
    public void setUp() {
        inquiry = new ExternalInquiry(INQUIRER_NAME, INQUIRER_CONTACT, INQUIRY_DETAILS);
        inquiry.setInquiryID(INQUIRY_ID);
        inquiry.setStatus(STATUS);
        inquiry.setResolution(RESOLUTION);
    }

    @Test
    public void testConstructorSetsInquirerName() {
        assertEquals("Inquirer name should be set in constructor", INQUIRER_NAME, inquiry.getInquirerName());
    }

    @Test
    public void testConstructorSetsInquirerContact() {
        assertEquals("Inquirer contact should be set in constructor", INQUIRER_CONTACT, inquiry.getInquirerContact());
    }

    @Test
    public void testConstructorSetsInquiryDetails() {
        assertEquals("Inquiry details should be set in constructor", INQUIRY_DETAILS, inquiry.getInquiryDetails());
    }

    @Test
    public void testConstructorSetsInitialStatus() {
        ExternalInquiry newInquiry = new ExternalInquiry(INQUIRER_NAME, INQUIRER_CONTACT, INQUIRY_DETAILS);
        assertEquals("Initial status should be 'pending'", "pending", newInquiry.getStatus());
    }

    @Test
    public void testConstructorSetsCurrentDate() {
        assertEquals("Inquiry date should be set to current date", LocalDate.now(), inquiry.getInquiryDate());
    }

    @Test
    public void testSetInquiryIdWithValidValue() {
        int newId = 2;
        inquiry.setInquiryID(newId);
        assertEquals("Inquiry ID should be updated", newId, inquiry.getInquiryID());
    }

    @Test
    public void testSetStatusWithValidValue() {
        String newStatus = "Resolved";
        inquiry.setStatus(newStatus);
        assertEquals("Status should be updated", newStatus, inquiry.getStatus());
    }

    @Test
    public void testSetResolutionWithValidValue() {
        String newResolution = "Found safe";
        inquiry.setResolution(newResolution);
        assertEquals("Resolution should be updated", newResolution, inquiry.getResolution());
    }

    @Test
    public void testIsValidInquirerNameWithValidName() {
        assertTrue("Valid inquirer name should return true", inquiry.isValidInquirerName());
    }

    @Test
    public void testIsValidInquirerContactWithValidContact() {
        assertTrue("Valid inquirer contact should return true", inquiry.isValidInquirerContact());
    }

    @Test
    public void testIsValidInquiryDetailsWithValidDetails() {
        assertTrue("Valid inquiry details should return true", inquiry.isValidInquiryDetails());
    }

    @Test
    public void testToStringContainsAllFields() {
        String str = inquiry.toString();
        assertTrue("Should contain inquiry ID", str.contains(String.valueOf(INQUIRY_ID)));
        assertTrue("Should contain inquirer name", str.contains(INQUIRER_NAME));
        assertTrue("Should contain inquirer contact", str.contains(INQUIRER_CONTACT));
        assertTrue("Should contain inquiry details", str.contains(INQUIRY_DETAILS));
        assertTrue("Should contain status", str.contains(STATUS));
        assertTrue("Should contain resolution", str.contains(RESOLUTION));
    }
} 