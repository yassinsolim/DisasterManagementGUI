package edu.ucalgary.oop;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.ucalgary.oop.Inquiry;

import java.time.LocalDate;

/**
 * Test class for the Inquiry class.
 * @author Yassin Soliman
 * @version 2025-04-13
 */
public class InquiryTest {
    private Inquiry inquiry;
    private final LocalDate testDate = LocalDate.of(2024, 4, 15);
    private final String testComments = "Test inquiry comments";

    @Before
    public void setUp() {
        inquiry = new Inquiry(testComments, testDate);
    }

    @Test
    public void testDefaultConstructor() {
        Inquiry emptyInquiry = new Inquiry();
        assertNotNull("Default constructor should create a non-null object", emptyInquiry);
        assertNull("Comments should be null for default constructor", emptyInquiry.getComments());
        assertNull("Date of inquiry should be null for default constructor", emptyInquiry.getDateOfInquiry());
    }

    @Test
    public void testParameterizedConstructor() {
        assertEquals("Comments should match constructor parameter", testComments, inquiry.getComments());
        assertEquals("Date should match constructor parameter", testDate, inquiry.getDateOfInquiry());
    }

    @Test
    public void testSetAndGetId() {
        int testId = 123;
        inquiry.setId(testId);
        assertEquals("ID should match the set value", testId, inquiry.getId());
    }

    @Test
    public void testSetAndGetInquirerId() {
        Integer testInquirerId = 456;
        inquiry.setInquirerId(testInquirerId);
        assertEquals("Inquirer ID should match the set value", testInquirerId, inquiry.getInquirerId());
        
        inquiry.setInquirerId(null);
        assertNull("Inquirer ID should be nullable", inquiry.getInquirerId());
    }

    @Test
    public void testSetAndGetSeekingId() {
        Integer testSeekingId = 789;
        inquiry.setSeekingId(testSeekingId);
        assertEquals("Seeking ID should match the set value", testSeekingId, inquiry.getSeekingId());
        
        inquiry.setSeekingId(null);
        assertNull("Seeking ID should be nullable", inquiry.getSeekingId());
    }

    @Test
    public void testSetAndGetLocationId() {
        Integer testLocationId = 101;
        inquiry.setLocationId(testLocationId);
        assertEquals("Location ID should match the set value", testLocationId, inquiry.getLocationId());
        
        inquiry.setLocationId(null);
        assertNull("Location ID should be nullable", inquiry.getLocationId());
    }

    @Test
    public void testSetAndGetDateOfInquiry() {
        LocalDate newDate = LocalDate.of(2024, 4, 16);
        inquiry.setDateOfInquiry(newDate);
        assertEquals("Date of inquiry should match the set value", newDate, inquiry.getDateOfInquiry());
        
        inquiry.setDateOfInquiry(null);
        assertNull("Date of inquiry should be nullable", inquiry.getDateOfInquiry());
    }

    @Test
    public void testSetAndGetComments() {
        String newComments = "Updated test comments";
        inquiry.setComments(newComments);
        assertEquals("Comments should match the set value", newComments, inquiry.getComments());
        
        inquiry.setComments(null);
        assertNull("Comments should be nullable", inquiry.getComments());
    }

    @Test
    public void testToString() {
        inquiry.setId(1);
        String expected = "Inquiry #1: " + testComments;
        assertEquals("toString should return the expected format", expected, inquiry.toString());
    }
} 