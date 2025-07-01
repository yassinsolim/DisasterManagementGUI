package edu.ucalgary.oop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import edu.ucalgary.oop.DisasterVictim;
import edu.ucalgary.oop.Location;
import edu.ucalgary.oop.Supply;
import edu.ucalgary.oop.Item;

/**
 * A mock implementation of database operations for testing purposes.
 * This class provides in-memory storage for victims, locations, supplies, and items.
 */
public class MockDatabaseAdapter implements AutoCloseable {
    private List<DisasterVictim> victims;
    private List<Location> locations;
    private List<Supply> supplies;
    private List<Item> items;
    private Map<Integer, List<Integer>> personLocations; // Maps personId to list of locationIds
    private int nextVictimId = 1;
    private int nextLocationId = 1;
    private int nextSupplyId = 1;
    private int nextItemId = 1;

    public MockDatabaseAdapter() {
        victims = new ArrayList<>();
        locations = new ArrayList<>();
        supplies = new ArrayList<>();
        items = new ArrayList<>();
        personLocations = new HashMap<>();
    }

    public int addVictim(DisasterVictim victim) throws SQLException {
        if (victim.getSocialID() == 0) {
            victim.setSocialID(nextVictimId++);
        }
        victims.add(victim);
        return victim.getSocialID();
    }

    public DisasterVictim getVictimById(int id) throws SQLException {
        return victims.stream()
                .filter(v -> v.getSocialID() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean addLocation(Location location) throws SQLException {
        location.setLocationId(nextLocationId++);
        return locations.add(location);
    }

    public List<Location> getAllLocations() throws SQLException {
        return new ArrayList<>(locations);
    }

    public boolean updateVictim(int victimId, DisasterVictim victim) throws SQLException {
        for (int i = 0; i < victims.size(); i++) {
            if (victims.get(i).getSocialID() == victimId) {
                victim.setSocialID(victimId);
                victims.set(i, victim);
                return true;
            }
        }
        return false;
    }

    public boolean deleteVictim(int id) throws SQLException {
        return victims.removeIf(v -> v.getSocialID() == id);
    }

    public List<DisasterVictim> getAllVictims() throws SQLException {
        return new ArrayList<>(victims);
    }

    public int addSupply(Supply supply) throws SQLException {
        supply.setId(nextSupplyId++);
        supplies.add(supply);
        return supply.getId();
    }

    public List<Supply> getAllSupplies() throws SQLException {
        return new ArrayList<>(supplies);
    }

    public boolean updateSupply(Supply supply) throws SQLException {
        for (int i = 0; i < supplies.size(); i++) {
            if (supplies.get(i).getId() == supply.getId()) {
                supplies.set(i, supply);
                return true;
            }
        }
        return false;
    }

    public boolean deleteSupply(int id) throws SQLException {
        return supplies.removeIf(s -> s.getId() == id);
    }

    public Item getItem(int itemId) {
        return items.stream()
                .filter(i -> i.getItemID() == itemId)
                .findFirst()
                .orElse(null);
    }

    public int addItem(Item item) {
        item.setItemID(nextItemId);
        items.add(item);
        return nextItemId++;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public boolean updateItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemID() == item.getItemID()) {
                items.set(i, item);
                return true;
            }
        }
        return false;
    }

    public boolean deleteItem(int itemId) {
        return items.removeIf(i -> i.getItemID() == itemId);
    }

    public boolean assignPersonToLocation(int personId, int locationId) throws SQLException {
        // Verify person and location exist
        if (getVictimById(personId) == null || 
            locations.stream().noneMatch(l -> l.getLocationId() == locationId)) {
            return false;
        }
        
        // Initialize list if needed
        personLocations.computeIfAbsent(personId, k -> new ArrayList<>());
        
        // Add location if not already assigned
        List<Integer> personLocs = personLocations.get(personId);
        if (!personLocs.contains(locationId)) {
            personLocs.add(locationId);
            return true;
        }
        return false;
    }

    public List<Location> getPersonLocations(int personId) throws SQLException {
        List<Location> result = new ArrayList<>();
        List<Integer> locationIds = personLocations.get(personId);
        
        if (locationIds != null) {
            for (Integer locId : locationIds) {
                locations.stream()
                    .filter(l -> l.getLocationId() == locId)
                    .findFirst()
                    .ifPresent(result::add);
            }
        }
        
        return result;
    }

    public void cleanupDuplicateVictims() throws SQLException {
        List<DisasterVictim> uniqueVictims = new ArrayList<>();
        List<Integer> processedIds = new ArrayList<>();
        
        // First pass: add all victims to uniqueVictims
        for (DisasterVictim victim : victims) {
            if (!processedIds.contains(victim.getSocialID())) {
                uniqueVictims.add(victim);
                processedIds.add(victim.getSocialID());
            }
        }
        
        // Second pass: remove duplicates but keep the first occurrence
        for (int i = 0; i < uniqueVictims.size(); i++) {
            DisasterVictim victim = uniqueVictims.get(i);
            for (int j = i + 1; j < uniqueVictims.size(); j++) {
                DisasterVictim other = uniqueVictims.get(j);
                if (victim.getFirstName().equals(other.getFirstName()) &&
                    victim.getLastName().equals(other.getLastName()) &&
                    victim.getDateOfBirth().equals(other.getDateOfBirth()) &&
                    victim.getGender().equals(other.getGender())) {
                    uniqueVictims.remove(j);
                    j--; // Adjust index after removal
                }
            }
        }
        
        // Update victims list with unique entries
        victims.clear();
        victims.addAll(uniqueVictims);
    }

    public boolean assignLocation(int victimId, int locationId) throws SQLException {
        // Verify victim and location exist
        if (getVictimById(victimId) == null || 
            locations.stream().noneMatch(l -> l.getLocationId() == locationId)) {
            return false;
        }
        
        // Initialize list if needed
        personLocations.computeIfAbsent(victimId, k -> new ArrayList<>());
        
        // Add location if not already assigned
        List<Integer> victimLocs = personLocations.get(victimId);
        if (!victimLocs.contains(locationId)) {
            victimLocs.add(locationId);
            return true;
        }
        return false;
    }

    public void initializeDatabase() throws SQLException {
        // No-op for mock implementation
    }

    public void cleanupExpiredData() throws SQLException {
        // No-op for mock implementation
    }

    public void logError(String message) {
        System.err.println("Mock Database Error: " + message);
    }

    public void removeAllLocationReferences(int locationId) throws SQLException {
        // Remove location from all person assignments
        personLocations.values().forEach(locs -> locs.remove(Integer.valueOf(locationId)));
        
        // Remove empty lists
        personLocations.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void close() throws SQLException {
        // No-op for mock implementation
    }

    public void closeConnection() {
        // No need to implement for mock
    }

    @Override
    public String toString() {
        return "MockDatabaseAdapter[victims=" + victims.size() + 
               ", locations=" + locations.size() + 
               ", supplies=" + supplies.size() + 
               ", items=" + items.size() + "]";
    }
} 