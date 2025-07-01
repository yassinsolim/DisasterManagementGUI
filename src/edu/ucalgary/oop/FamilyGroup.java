package edu.ucalgary.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a family group in the disaster management system.
 * A family group consists of multiple disaster victims who are related,
 * allowing for better organization and tracking of family units.
 */
public class FamilyGroup {
    private int groupID;
    private List<DisasterVictim> members;

    /**
     * Creates a new family group with the specified ID.
     * Initializes an empty list of members.
     *
     * @param groupID The unique identifier for the family group
     */
    public FamilyGroup(int groupID) {
        this.groupID = groupID;
        this.members = new ArrayList<>();
    }

    /**
     * Gets the family group's unique identifier.
     *
     * @return The group ID
     */
    public int getGroupID() { return groupID; }

    /**
     * Sets the family group's unique identifier.
     *
     * @param id The group ID to set
     */
    public void setGroupID(int id) { this.groupID = id; }

    /**
     * Gets a copy of the list of family members.
     *
     * @return A new ArrayList containing the family members
     */
    public List<DisasterVictim> getMembers() { return new ArrayList<>(members); }

    /**
     * Adds a disaster victim to this family group.
     * If the victim is not already a member, they are added and their
     * family group reference is updated.
     *
     * @param member The disaster victim to add
     */
    public void addMember(DisasterVictim member) {
        if (!members.contains(member)) {
            members.add(member);
            member.setFamilyGroup(this);
        }
    }

    /**
     * Removes a disaster victim from this family group.
     * The victim's family group reference is cleared.
     *
     * @param member The disaster victim to remove
     */
    public void removeMember(DisasterVictim member) {
        members.remove(member);
        if (member != null) {
            member.setFamilyGroup(null);
        }
    }

    /**
     * Returns a string representation of the family group.
     * Includes the group ID, number of members, and a list of member names.
     *
     * @return A formatted string describing the family group
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Family Group #%d (%d members):\n", groupID, members.size()));
        for (DisasterVictim member : members) {
            sb.append(String.format("- %s %s\n", member.getFirstName(), member.getLastName()));
        }
        return sb.toString();
    }

    /**
     * Checks if this family group is equal to another object.
     * Family groups are considered equal if they have the same ID and
     * contain the same members in the same order.
     *
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FamilyGroup other = (FamilyGroup) obj;
        if (!Objects.equals(groupID, other.groupID)) return false;
        if (members.size() != other.members.size()) return false;
        for (int i = 0; i < members.size(); i++) {
            if (!members.get(i).equals(other.members.get(i))) return false;
        }
        return true;
    }

    /**
     * Generates a hash code for this family group.
     * The hash code is based on the group ID and the list of members.
     *
     * @return A hash code for this family group
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupID, members);
    }
} 