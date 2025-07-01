package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Test;

public class ItemTypeTest {
    
    @Test
    public void testWaterType() {
        assertEquals("WATER", ItemType.WATER.name());
    }
    
    @Test
    public void testBlanketType() {
        assertEquals("BLANKET", ItemType.BLANKET.name());
    }
    
    @Test
    public void testCotType() {
        assertEquals("COT", ItemType.COT.name());
    }
    
    @Test
    public void testPersonalBelongingType() {
        assertEquals("PERSONAL_BELONGING", ItemType.PERSONAL_BELONGING.name());
    }
    
    @Test
    public void testValueOf() {
        assertEquals(ItemType.WATER, ItemType.valueOf("WATER"));
        assertEquals(ItemType.BLANKET, ItemType.valueOf("BLANKET"));
        assertEquals(ItemType.COT, ItemType.valueOf("COT"));
        assertEquals(ItemType.PERSONAL_BELONGING, ItemType.valueOf("PERSONAL_BELONGING"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidType() {
        ItemType.valueOf("INVALID_TYPE");
    }
} 