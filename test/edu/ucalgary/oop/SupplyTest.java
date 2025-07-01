package edu.ucalgary.oop;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SupplyTest {
    private Supply supply;
    private static final int TEST_ID = 1;
    private static final String TEST_TYPE = "Water";
    private static final String TEST_COMMENTS = "Emergency drinking water";

    @Before
    public void setUp() {
        // Arrange - Common setup
        supply = new Supply(TEST_ID, TEST_TYPE, TEST_COMMENTS);
    }

    @Test
    public void testConstructorWithIdSetsId() {
        // Assert
        assertEquals("Supply ID should match constructor value", TEST_ID, supply.getId());
    }

    @Test
    public void testConstructorWithIdSetsType() {
        // Assert
        assertEquals("Supply type should match constructor value", TEST_TYPE, supply.getType());
    }

    @Test
    public void testConstructorWithIdSetsComments() {
        // Assert
        assertEquals("Supply comments should match constructor value", TEST_COMMENTS, supply.getComments());
    }

    @Test
    public void testConstructorWithoutIdSetsType() {
        // Arrange
        Supply supplyNoId = new Supply(TEST_TYPE, TEST_COMMENTS);
        
        // Assert
        assertEquals("Supply type should match constructor value", TEST_TYPE, supplyNoId.getType());
    }

    @Test
    public void testConstructorWithoutIdSetsComments() {
        // Arrange
        Supply supplyNoId = new Supply(TEST_TYPE, TEST_COMMENTS);
        
        // Assert
        assertEquals("Supply comments should match constructor value", TEST_COMMENTS, supplyNoId.getComments());
    }

    @Test
    public void testSetTypeWithValidInput() {
        // Arrange
        String newType = "Food";
        
        // Act
        supply.setType(newType);
        
        // Assert
        assertEquals("Supply type should be updated", newType, supply.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTypeWithNull() {
        // Act
        supply.setType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTypeWithEmptyString() {
        // Act
        supply.setType("");
    }

    @Test
    public void testSetCommentsWithValidInput() {
        // Arrange
        String newComments = "Updated comments";
        
        // Act
        supply.setComments(newComments);
        
        // Assert
        assertEquals("Supply comments should be updated", newComments, supply.getComments());
    }

    @Test
    public void testSetCommentsWithNull() {
        // Act
        supply.setComments(null);
        
        // Assert
        assertNull("Supply comments should be set to null", supply.getComments());
    }

    @Test
    public void testSetIdWithPositiveValue() {
        // Arrange
        int newId = 2;
        
        // Act
        supply.setId(newId);
        
        // Assert
        assertEquals("Supply ID should be updated", newId, supply.getId());
    }

    @Test
    public void testSetIdWithZero() {
        // Act
        supply.setId(0);
        
        // Assert
        assertEquals("Supply ID should allow zero", 0, supply.getId());
    }

    @Test
    public void testConstructorWithEmptyComments() {
        // Arrange
        Supply supplyEmptyComments = new Supply(TEST_TYPE, "");
        
        // Assert
        assertEquals("Empty comments should be allowed", "", supplyEmptyComments.getComments());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullType() {
        // Act
        new Supply(null, TEST_COMMENTS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyType() {
        // Act
        new Supply("", TEST_COMMENTS);
    }

    @Test
    public void testConstructorWithNullComments() {
        // Arrange
        Supply supplyNullComments = new Supply(TEST_TYPE, null);
        
        // Assert
        assertNull("Null comments should be allowed", supplyNullComments.getComments());
    }
} 