package edu.ucalgary.oop;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FamilyGroupTest {
    private FamilyGroup familyGroup;
    private DisasterVictim victim1;
    private DisasterVictim victim2;

    @Before
    public void setUp() {
        familyGroup = new FamilyGroup(1);
        victim1 = new DisasterVictim("John", "Doe", LocalDate.of(1990, 1, 1));
        victim2 = new DisasterVictim("Jane", "Doe", LocalDate.of(1992, 2, 2));
    }

    @Test
    public void testConstructor() {
        assertEquals("Group ID should be 1", 1, familyGroup.getGroupID());
        assertTrue("Members list should be empty", familyGroup.getMembers().isEmpty());
    }

    @Test
    public void testAddMember() {
        familyGroup.addMember(victim1);
        assertEquals("Group should have 1 member", 1, familyGroup.getMembers().size());
        assertTrue("Group should contain victim1", familyGroup.getMembers().contains(victim1));
        assertEquals("Victim's family group should be set", familyGroup, victim1.getFamilyGroup());
    }

    @Test
    public void testRemoveMember() {
        familyGroup.addMember(victim1);
        familyGroup.removeMember(victim1);
        assertTrue("Group should be empty", familyGroup.getMembers().isEmpty());
        assertNull("Victim's family group should be null", victim1.getFamilyGroup());
    }

    @Test
    public void testMultipleMembers() {
        familyGroup.addMember(victim1);
        familyGroup.addMember(victim2);
        assertEquals("Group should have 2 members", 2, familyGroup.getMembers().size());
        assertTrue("Group should contain victim1", familyGroup.getMembers().contains(victim1));
        assertTrue("Group should contain victim2", familyGroup.getMembers().contains(victim2));
    }
} 