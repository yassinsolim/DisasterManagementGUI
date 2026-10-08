package edu.ucalgary.oop;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * DatabaseAdapter.java
 * 
 * A singleton class that provides an interface for database operations in the Disaster Management System.
 * This class handles all database interactions including managing victims, supplies, inquiries,
 * locations, and medical records.
 * 
 * @author Yassin Soliman
 * @version 1.0
 * @since 2025-04-13
 * @see AutoCloseable
 * @see Connection
 * @see DisasterVictim
 * @see Supply
 * @see Inquiry
 * @see Location
 * @see MedicalRecord
 */
public class DatabaseAdapter implements AutoCloseable {
    private static DatabaseAdapter instance;
    private Connection connection;
    private static final String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/ensf380project");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "oop");
    private static final String PASS = System.getenv("DB_PASSWORD");
    private boolean isTestMode = false;
    
    // Test mode data
    private List<DisasterVictim> testVictims = new ArrayList<>();
    private int nextTestVictimId = 1;

    protected String getDbUrl() {
        return DB_URL;
    }

    private DatabaseAdapter(boolean isTestMode) throws SQLException {
        this.isTestMode = isTestMode;
        if (!isTestMode) {
            initializeConnection();
        }
    }

    private void initializeConnection() throws SQLException {
        if (isTestMode) {
            return; // Don't initialize connection in test mode
        }
        
        if (PASS == null) {
            throw new SQLException("DB_PASSWORD is not set. Export DB_PASSWORD before connecting to "
                + getDbUrl() + "; set it to an empty value if the server uses trust authentication.");
        }

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            
            // Set connection properties
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            props.setProperty("ssl", "false");
            
            // Create the connection
            connection = DriverManager.getConnection(getDbUrl(), props);
            
            if (connection == null) {
                System.err.println("Failed to establish database connection: connection is null");
                throw new SQLException("Failed to establish database connection");
            }
            
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("PostgreSQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            new ErrorLogger().logError("Database connection error: " + e.getMessage());
            throw e;
        }
    }

    public static DatabaseAdapter createTestInstance() throws SQLException {
        return new DatabaseAdapter(true);
    }

    public static DatabaseAdapter createInstance() throws SQLException {
        return new DatabaseAdapter(false);
    }

    public static DatabaseAdapter getInstance() throws SQLException {
        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    public ResultSet fetch(String query) throws SQLException {
        if (isTestMode) {
            return null;
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    public int update(String sql) throws SQLException {
        if (isTestMode) {
            return 0;
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        Statement stmt = connection.createStatement();
        return stmt.executeUpdate(sql);
    }

    // ✅ Aliases for compatibility
    public int saveData(String sql) throws SQLException {
        return update(sql);
    }

    public int updateData(String sql) throws SQLException {
        return update(sql);
    }

    // External Inquiry Methods
    public int addExternalInquiry(ExternalInquiry inquiry) {
        if (isTestMode) {
            return 1;
        }
        String sql = "INSERT INTO Inquiry (inquirer_id, seeking_id, location_id, date_of_inquiry, comments) VALUES (NULL, NULL, NULL, ?, ?) RETURNING inquiry_id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(inquiry.getInquiryDate().atStartOfDay()));
            pstmt.setString(2, inquiry.getInquiryDetails());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("inquiry_id");
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error adding external inquiry: " + e.getMessage());
        }
        return -1;
    }

    public List<ExternalInquiry> getExternalInquiries() {
        if (isTestMode) {
            return new ArrayList<>();
        }
        List<ExternalInquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry WHERE inquirer_id = 0 ORDER BY date_of_inquiry DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ExternalInquiry inquiry = new ExternalInquiry(
                    "External Inquirer", // We don't have the name in the database
                    "Unknown", // We don't have the contact in the database
                    rs.getString("comments")
                );
                inquiry.setInquiryID(rs.getInt("inquiry_id"));
                inquiry.setStatus("pending"); // We don't have status in the database
                inquiries.add(inquiry);
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error getting external inquiries: " + e.getMessage());
        }
        return inquiries;
    }

    public boolean updateExternalInquiry(ExternalInquiry inquiry) {
        if (isTestMode) {
            return true;
        }
        String sql = "UPDATE Inquiry SET comments = ? WHERE inquiry_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inquiry.getInquiryDetails() + " [Status: " + inquiry.getStatus() + 
                           ", Resolution: " + (inquiry.getResolution() != null ? inquiry.getResolution() : "Not resolved") + "]");
            pstmt.setInt(2, inquiry.getInquiryID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error updating external inquiry: " + e.getMessage());
            return false;
        }
    }

    // Victim Inquiry Methods
    public int addVictimInquiry(VictimInquiry inquiry) {
        String sql = "INSERT INTO Inquiry (inquirer_id, seeking_id, location_id, date_of_inquiry, comments) VALUES (?, NULL, NULL, ?, ?) RETURNING inquiry_id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // For victim inquiries, the inquirer is the victim
            pstmt.setInt(1, inquiry.getPersonID());
            pstmt.setTimestamp(2, Timestamp.valueOf(inquiry.getInquiryDate().atStartOfDay()));
            pstmt.setString(3, inquiry.getInquiryDetails());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("inquiry_id");
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error adding victim inquiry: " + e.getMessage());
        }
        return -1;
    }

    public List<VictimInquiry> getVictimInquiries() {
        List<VictimInquiry> inquiries = new ArrayList<>();
        String sql = "SELECT * FROM Inquiry WHERE inquirer_id > 0 AND seeking_id = 0 ORDER BY date_of_inquiry DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                VictimInquiry inquiry = new VictimInquiry(
                    rs.getInt("inquirer_id"),
                    rs.getString("comments")
                );
                inquiry.setInquiryID(rs.getInt("inquiry_id"));
                inquiry.setStatus("pending"); // We don't have status in the database
                inquiries.add(inquiry);
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error getting victim inquiries: " + e.getMessage());
        }
        return inquiries;
    }

    public boolean updateVictimInquiry(VictimInquiry inquiry) {
        String sql = "UPDATE Inquiry SET comments = ? WHERE inquiry_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, inquiry.getInquiryDetails() + " [Status: " + inquiry.getStatus() + 
                           ", Resolution: " + (inquiry.getResolution() != null ? inquiry.getResolution() : "Not resolved") + "]");
            pstmt.setInt(2, inquiry.getInquiryID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error updating victim inquiry: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteInquiry(int inquiryId) {
        String sql = "DELETE FROM Inquiry WHERE inquiry_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, inquiryId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error deleting inquiry: " + e.getMessage());
            return false;
        }
    }

    // Search Methods
    public List<DisasterVictim> searchVictims(String firstName, String lastName, String gender) throws SQLException {
        List<DisasterVictim> results = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Person WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            query.append(" AND LOWER(first_name) LIKE LOWER(?)");
            params.add("%" + firstName.trim() + "%");
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            query.append(" AND LOWER(last_name) LIKE LOWER(?)");
            params.add("%" + lastName.trim() + "%");
        }
        
        if (gender != null && !gender.trim().isEmpty()) {
            query.append(" AND LOWER(gender) = LOWER(?)");
            params.add(gender.trim());
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                DisasterVictim victim = new DisasterVictim(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDate("date_of_birth").toLocalDate()
                );
                victim.setGender(rs.getString("gender"));
                victim.setComments(rs.getString("comments"));
                victim.setPhoneNumber(rs.getString("phone_number"));
                // Only set family group if it's not null
                int familyGroupId = rs.getInt("family_group");
                if (!rs.wasNull()) {
                    FamilyGroup familyGroup = new FamilyGroup(familyGroupId);
                    victim.setFamilyGroup(familyGroup);
                }
                results.add(victim);
            }
        }
        return results;
    }

    public int addVictim(DisasterVictim victim) throws SQLException {
        if (isTestMode) {
            victim.setId(nextTestVictimId++);
            testVictims.add(victim);
            return victim.getId();
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        
        connection.setAutoCommit(false);
        try {
            String sql = "INSERT INTO Person (first_name, last_name, date_of_birth, gender, comments, phone_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING person_id";
            int personId;
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, victim.getFirstName());
                pstmt.setString(2, victim.getLastName());
                pstmt.setDate(3, java.sql.Date.valueOf(victim.getDateOfBirth()));
                pstmt.setString(4, victim.getGender());
                pstmt.setString(5, victim.getComments());
                pstmt.setString(6, victim.getPhoneNumber());
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    personId = rs.getInt("person_id");
                    
                    // If location is provided, add it to PersonLocation table
                    if (victim.getLocation() != null) {
                        sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";
                        try (PreparedStatement locStmt = connection.prepareStatement(sql)) {
                            locStmt.setInt(1, personId);
                            locStmt.setInt(2, victim.getLocation().getId());
                            locStmt.executeUpdate();
                        }
                    }
                    
                    connection.commit();
                    return personId;
                }
            }
            connection.rollback();
            return -1;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                new ErrorLogger().logError("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            new ErrorLogger().logError("Error adding victim: " + e.getMessage());
            return -1;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                new ErrorLogger().logError("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean updateVictim(int victimId, DisasterVictim victim) throws SQLException {
        if (isTestMode) {
            for (int i = 0; i < testVictims.size(); i++) {
                if (testVictims.get(i).getId() == victimId) {
                    victim.setId(victimId);
                    testVictims.set(i, victim);
                    return true;
                }
            }
            return false;
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        
        connection.setAutoCommit(false);
        try {
            String sql = "UPDATE Person SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ? WHERE person_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, victim.getFirstName());
                stmt.setString(2, victim.getLastName());
                stmt.setDate(3, java.sql.Date.valueOf(victim.getDateOfBirth()));
                stmt.setString(4, victim.getGender());
                stmt.setInt(5, victimId);
                stmt.executeUpdate();
            }

            if (victim.getLocation() != null) {
                sql = "DELETE FROM PersonLocation WHERE person_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, victimId);
                    stmt.executeUpdate();
                }

                sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setInt(1, victimId);
                    stmt.setInt(2, victim.getLocation().getId());
                    stmt.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean deleteVictim(int victimId) throws SQLException {
        if (isTestMode) {
            return testVictims.removeIf(v -> v.getId() == victimId);
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        
        try {
            connection.setAutoCommit(false);
            
            // Remove from PersonLocation
            String deletePersonLocation = "DELETE FROM PersonLocation WHERE person_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deletePersonLocation)) {
                pstmt.setInt(1, victimId);
                pstmt.executeUpdate();
            }
            
            // Remove from MedicalRecord
            String deleteMedicalRecords = "DELETE FROM MedicalRecord WHERE person_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteMedicalRecords)) {
                pstmt.setInt(1, victimId);
                pstmt.executeUpdate();
            }
            
            // Remove from Inquiry (both as inquirer and seeking)
            String deleteInquiries = "DELETE FROM Inquiry WHERE inquirer_id = ? OR seeking_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteInquiries)) {
                pstmt.setInt(1, victimId);
                pstmt.setInt(2, victimId);
                pstmt.executeUpdate();
            }
            
            // Finally, delete the victim
            String deleteVictim = "DELETE FROM Person WHERE person_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteVictim)) {
                pstmt.setInt(1, victimId);
                int result = pstmt.executeUpdate();
                connection.commit();
                return result > 0;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                new ErrorLogger().logError("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            new ErrorLogger().logError("Error deleting victim: " + e.getMessage());
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                new ErrorLogger().logError("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public List<DisasterVictim> getAllVictims() throws SQLException {
        if (isTestMode) {
            return new ArrayList<>(testVictims);
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        
        List<DisasterVictim> victims = new ArrayList<>();
        String sql = "SELECT p.*, l.name as location_name, l.address as location_address, l.location_id " +
                    "FROM Person p " +
                    "LEFT JOIN PersonLocation pl ON p.person_id = pl.person_id " +
                    "LEFT JOIN Location l ON pl.location_id = l.location_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DisasterVictim victim = new DisasterVictim(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null
                );
                victim.setId(rs.getInt("person_id"));
                victim.setGender(rs.getString("gender"));
                
                // Set location if exists
                int locationId = rs.getInt("location_id");
                if (!rs.wasNull()) {
                    Location location = new Location(
                        rs.getString("location_name"),
                        rs.getString("location_address")
                    );
                    location.setLocationId(locationId);
                    victim.setLocation(location);
                }
                
                victims.add(victim);
            }
        }
        return victims;
    }

    @Override
    public void close() throws SQLException {
        closeConnection();
    }

    public void closeConnection() {
        if (!isTestMode && connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                new ErrorLogger().logError("Error closing connection: " + e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "DatabaseAdapter: Connected to ensf380project";
    }

    public Connection getConnection() {
        if (isTestMode) {
            return null;
        }
        return connection;
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error deleting item: " + e.getMessage());
            return false;
        }
    }

    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (item_type, description, quantity, expiry_date, room, grid) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getItemType().name());
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getQuantity());
            if (item.getDate() != null && !item.getDate().equals("null")) {
                pstmt.setDate(4, java.sql.Date.valueOf(item.getDate().substring(0, 4) + "-" + 
                                                      item.getDate().substring(4, 6) + "-" + 
                                                      item.getDate().substring(6, 8)));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }
            pstmt.setString(5, item.getRoom());
            pstmt.setString(6, item.getGrid());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error adding item: " + e.getMessage());
            return false;
        }
    }

    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET item_type = ?, description = ?, quantity = ?, expiry_date = ?, room = ?, grid = ? WHERE item_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, item.getItemType().name());
            pstmt.setString(2, item.getDescription());
            pstmt.setInt(3, item.getQuantity());
            if (item.getDate() != null && !item.getDate().equals("null")) {
                pstmt.setDate(4, java.sql.Date.valueOf(item.getDate().substring(0, 4) + "-" + 
                                                      item.getDate().substring(4, 6) + "-" + 
                                                      item.getDate().substring(6, 8)));
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }
            pstmt.setString(5, item.getRoom());
            pstmt.setString(6, item.getGrid());
            pstmt.setInt(7, item.getItemID());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            new ErrorLogger().logError("Error updating item: " + e.getMessage());
            return false;
        }
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String itemTypeStr = rs.getString("item_type");
                ItemType itemType;
                try {
                    itemType = Enum.valueOf(ItemType.class, itemTypeStr);
                } catch (IllegalArgumentException e) {
                    new ErrorLogger().logError("Invalid item type in database: " + itemTypeStr);
                    continue;
                }
                
                // Format the date from SQL Date to YYYYMMDD format
                String expiryDate = null;
                java.sql.Date sqlDate = rs.getDate("expiry_date");
                if (sqlDate != null) {
                    expiryDate = sqlDate.toString().replace("-", "");
                }
                
                Item item = new Item(
                    rs.getInt("item_id"),
                    itemType,
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    expiryDate,
                    rs.getString("room"),
                    rs.getString("grid")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error getting items: " + e.getMessage());
        }
        return items;
    }

    public Item getItem(int itemId) {
        String sql = "SELECT * FROM items WHERE item_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String itemTypeStr = rs.getString("item_type");
                ItemType itemType;
                try {
                    itemType = Enum.valueOf(ItemType.class, itemTypeStr);
                } catch (IllegalArgumentException e) {
                    new ErrorLogger().logError("Invalid item type in database: " + itemTypeStr);
                    return null;
                }
                
                // Format the date from SQL Date to YYYYMMDD format
                String expiryDate = null;
                java.sql.Date sqlDate = rs.getDate("expiry_date");
                if (sqlDate != null) {
                    expiryDate = sqlDate.toString().replace("-", "");
                }
                
                return new Item(
                    rs.getInt("item_id"),
                    itemType,
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    expiryDate,
                    rs.getString("room"),
                    rs.getString("grid")
                );
            }
        } catch (SQLException e) {
            new ErrorLogger().logError("Error getting item: " + e.getMessage());
        }
        return null;
    }

    public void cleanupDuplicateVictims() {
        try {
            // First, identify duplicates based on first_name, last_name, date_of_birth, and gender
            String findDuplicates = 
                "WITH duplicates AS (" +
                "  SELECT first_name, last_name, date_of_birth, gender, COUNT(*) as count, " +
                "         MIN(victim_id) as keep_id " +
                "  FROM victims " +
                "  GROUP BY first_name, last_name, date_of_birth, gender " +
                "  HAVING COUNT(*) > 1" +
                ") " +
                "SELECT v.victim_id " +
                "FROM victims v " +
                "JOIN duplicates d ON v.first_name = d.first_name " +
                "  AND v.last_name = d.last_name " +
                "  AND v.date_of_birth = d.date_of_birth " +
                "  AND v.gender = d.gender " +
                "WHERE v.victim_id != d.keep_id";
            
            // Get the IDs of victims to delete
            List<Integer> idsToDelete = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(findDuplicates);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    idsToDelete.add(rs.getInt("victim_id"));
                }
            }
            
            // Delete the duplicate victims
            if (!idsToDelete.isEmpty()) {
                String deleteDuplicates = "DELETE FROM victims WHERE victim_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deleteDuplicates)) {
                    for (Integer id : idsToDelete) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cleaning up duplicate victims: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supply Management Methods
    public int addSupply(Supply supply) throws SQLException {
        String sql = "INSERT INTO Supply (type, comments) VALUES (?, ?) RETURNING supply_id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supply.getType());
            stmt.setString(2, supply.getComments());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                supply.setId(id);
                return id;
            }
            return -1;
        }
    }

    public List<Supply> getAllSupplies() throws SQLException {
        List<Supply> supplies = new ArrayList<>();
        String sql = "SELECT supply_id, type, comments FROM Supply";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Supply supply = new Supply(rs.getString("type"), rs.getString("comments"));
                supply.setId(rs.getInt("supply_id"));
                supplies.add(supply);
            }
        }
        return supplies;
    }

    public boolean updateSupply(Supply supply) throws SQLException {
        String sql = "UPDATE Supply SET type = ?, comments = ? WHERE supply_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supply.getType());
            stmt.setString(2, supply.getComments());
            stmt.setInt(3, supply.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteSupply(int id) throws SQLException {
        String sql = "DELETE FROM Supply WHERE supply_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Inquiry Management Methods
    public List<Inquiry> getAllInquiries() throws SQLException {
        List<Inquiry> inquiries = new ArrayList<>();
        String sql = "SELECT i.*, p1.first_name as inquirer_first_name, p1.last_name as inquirer_last_name, " +
                    "p2.first_name as seeking_first_name, p2.last_name as seeking_last_name, " +
                    "l.name as location_name " +
                    "FROM Inquiry i " +
                    "LEFT JOIN Person p1 ON i.inquirer_id = p1.person_id " +
                    "LEFT JOIN Person p2 ON i.seeking_id = p2.person_id " +
                    "LEFT JOIN Location l ON i.location_id = l.location_id";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Inquiry inquiry = new Inquiry();
                inquiry.setId(rs.getInt("inquiry_id"));
                inquiry.setInquirerId(rs.getObject("inquirer_id") != null ? rs.getInt("inquirer_id") : null);
                inquiry.setSeekingId(rs.getObject("seeking_id") != null ? rs.getInt("seeking_id") : null);
                inquiry.setLocationId(rs.getObject("location_id") != null ? rs.getInt("location_id") : null);
                inquiry.setDateOfInquiry(rs.getTimestamp("date_of_inquiry") != null ? 
                    rs.getTimestamp("date_of_inquiry").toLocalDateTime().toLocalDate() : null);
                inquiry.setComments(rs.getString("comments"));
                inquiries.add(inquiry);
            }
        }
        return inquiries;
    }

    public int addInquiry(Inquiry inquiry) throws SQLException {
        String sql = "INSERT INTO Inquiry (inquirer_id, seeking_id, location_id, date_of_inquiry, comments) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING inquiry_id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, inquiry.getInquirerId());
            pstmt.setObject(2, inquiry.getSeekingId());
            pstmt.setObject(3, inquiry.getLocationId());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(5, inquiry.getComments());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    public void updateInquiry(Inquiry inquiry) throws SQLException {
        String sql = "UPDATE Inquiry SET inquirer_id = ?, seeking_id = ?, location_id = ?, comments = ? " +
                    "WHERE inquiry_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, inquiry.getInquirerId());
            pstmt.setObject(2, inquiry.getSeekingId());
            pstmt.setObject(3, inquiry.getLocationId());
            pstmt.setString(4, inquiry.getComments());
            pstmt.setInt(5, inquiry.getId());
            pstmt.executeUpdate();
        }
    }

    // Victim Management Methods
    public List<MedicalRecord> getMedicalRecords(int victimId) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        String sql = "SELECT m.*, l.name as location_name FROM MedicalRecord m " +
                    "LEFT JOIN Location l ON m.location_id = l.location_id " +
                    "WHERE m.person_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, victimId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecord record = new MedicalRecord(
                        rs.getInt("medical_record_id"),
                        rs.getInt("person_id"),
                        rs.getString("treatment_details"),
                        rs.getString("location_name") != null ? rs.getString("location_name") : "Unknown",
                        rs.getTimestamp("date_of_treatment").toLocalDateTime()
                    );
                    record.setLocationId(rs.getInt("location_id"));
                    records.add(record);
                }
            }
        }
        return records;
    }

    public boolean addMedicalRecord(int victimId, String treatment, LocalDate date, int locationId) throws SQLException {
        String sql = "INSERT INTO MedicalRecord (person_id, treatment_details, date_of_treatment, location_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, victimId);
            pstmt.setString(2, treatment);
            pstmt.setDate(3, Date.valueOf(date));
            pstmt.setInt(4, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMedicalRecord(int recordId) throws SQLException {
        String sql = "DELETE FROM MedicalRecord WHERE medical_record_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean assignLocation(int victimId, int locationId) throws SQLException {
        String sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, victimId);
            pstmt.setInt(2, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Location Management Methods
    public List<Location> getAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Location";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Location location = new Location(
                    rs.getString("name"),
                    rs.getString("address")
                );
                location.setLocationId(rs.getInt("location_id"));
                locations.add(location);
            }
        }
        return locations;
    }

    public boolean addLocation(Location location) throws SQLException {
        String sql = "INSERT INTO Location (name, address) VALUES (?, ?) RETURNING location_id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getName());
            pstmt.setString(2, location.getAddress());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                location.setLocationId(rs.getInt("location_id"));
                return true;
            }
        }
        return false;
    }

    public boolean updateLocation(Location location) throws SQLException {
        String sql = "UPDATE Location SET name = ?, address = ? WHERE location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getName());
            pstmt.setString(2, location.getAddress());
            pstmt.setInt(3, location.getLocationId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean assignPersonToLocation(int personId, int locationId) throws SQLException {
        String sql = "INSERT INTO PersonLocation (person_id, location_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            pstmt.setInt(2, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Location> getPersonLocations(int personId) throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT l.* FROM Location l JOIN PersonLocation pl ON l.location_id = pl.location_id WHERE pl.person_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, personId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Location location = new Location(
                    rs.getString("name"),
                    rs.getString("address")
                );
                location.setLocationId(rs.getInt("location_id"));
                locations.add(location);
            }
        }
        return locations;
    }

    public void initializeDatabase() throws SQLException {
        try {
            // Check if tables exist
            String checkTables = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'person')";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTables)) {
                if (!rs.next() || !rs.getBoolean(1)) {
                    throw new SQLException("Required tables do not exist. Please run project.sql first.");
                }
            }

            // Clean up expired/obsolete data
            cleanupExpiredData();
        } catch (SQLException e) {
            logError("Database initialization error: " + e.getMessage());
            throw e;
        }
    }

    private void cleanupExpiredData() throws SQLException {
        // Add any necessary cleanup logic here
        // For example, if you have any temporary or expired data that needs to be removed
    }

    private void logError(String message) {
        try {
            // Create data directory if it doesn't exist
            new File("data").mkdirs();
            
            // Append to error log
            try (FileWriter writer = new FileWriter("data/errorlog.txt", true)) {
                writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " - " + message + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to write to error log: " + e.getMessage());
        }
    }

    public DisasterVictim getVictimById(int victimId) throws SQLException {
        if (isTestMode) {
            return testVictims.stream()
                .filter(v -> v.getId() == victimId)
                .findFirst()
                .orElse(null);
        }
        if (connection == null) {
            throw new SQLException("No database connection available");
        }
        
        String sql = "SELECT p.*, l.name as location_name, l.address as location_address, l.location_id " +
                    "FROM Person p " +
                    "LEFT JOIN PersonLocation pl ON p.person_id = pl.person_id " +
                    "LEFT JOIN Location l ON pl.location_id = l.location_id " +
                    "WHERE p.person_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, victimId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DisasterVictim victim = new DisasterVictim(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth") != null ? rs.getDate("date_of_birth").toLocalDate() : null
                    );
                    victim.setId(victimId);
                    victim.setGender(rs.getString("gender"));
                    
                    // Set location if exists
                    int locationId = rs.getInt("location_id");
                    if (!rs.wasNull()) {
                        Location location = new Location(
                            rs.getString("location_name"),
                            rs.getString("location_address")
                        );
                        location.setLocationId(locationId);
                        victim.setLocation(location);
                    }
                    
                    return victim;
                }
            }
        }
        return null;
    }

    public boolean deleteLocation(int locationId) throws SQLException {
        // First, check if the location has any occupants
        String checkSql = "SELECT COUNT(*) FROM PersonLocation WHERE location_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, locationId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Location has occupants, cannot delete
            }
        }

        // If no occupants, proceed with deletion
        String sql = "DELETE FROM Location WHERE location_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void removeAllLocationReferences(int locationId) throws SQLException {
        // Remove location references from inquiries
        String updateInquiriesSQL = "UPDATE inquiry SET location_id = NULL WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateInquiriesSQL)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
        
        // Remove location references from medical records
        String updateMedicalRecordsSQL = "UPDATE medicalrecord SET location = NULL WHERE location = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateMedicalRecordsSQL)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
        
        // Remove location references from person_location
        String deletePersonLocationSQL = "DELETE FROM person_location WHERE location_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deletePersonLocationSQL)) {
            stmt.setInt(1, locationId);
            stmt.executeUpdate();
        }
    }

    public boolean updateMedicalRecord(MedicalRecord record) throws SQLException {
        String sql = "UPDATE MedicalRecord SET treatment_details = ?, date_of_treatment = ?, location_id = ? WHERE medical_record_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, record.getTreatmentDetails());
            stmt.setTimestamp(2, Timestamp.valueOf(record.getDateOfTreatment()));
            stmt.setInt(3, record.getLocationId());
            stmt.setInt(4, record.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean allocateSupply(int supplyId, int victimId, int quantity) throws SQLException {
        // First check if the supply exists and has enough quantity
        String checkSql = "SELECT type, comments FROM Supply WHERE supply_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, supplyId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return false; // Supply not found
            }
        }

        // Then check if the victim exists
        String checkVictimSql = "SELECT person_id FROM Person WHERE person_id = ?";
        try (PreparedStatement checkVictimStmt = connection.prepareStatement(checkVictimSql)) {
            checkVictimStmt.setInt(1, victimId);
            ResultSet rs = checkVictimStmt.executeQuery();
            if (!rs.next()) {
                return false; // Victim not found
            }
        }

        // Create allocation record
        String sql = "INSERT INTO SupplyAllocation (supply_id, person_id, quantity, allocation_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, supplyId);
            stmt.setInt(2, victimId);
            stmt.setInt(3, quantity);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    public Location getVictimLocation(int victimId) throws SQLException {
        String sql = "SELECT l.* FROM Location l " +
                    "JOIN PersonLocation pl ON l.location_id = pl.location_id " +
                    "WHERE pl.person_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, victimId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Location location = new Location(
                    rs.getString("name"),
                    rs.getString("address")
                );
                location.setLocationId(rs.getInt("location_id"));
                return location;
            }
        }
        return null;
    }

    public boolean assignVictimToLocation(int victimId, int locationId) throws SQLException {
        // First remove any existing location assignments
        String deleteSql = "DELETE FROM PersonLocation WHERE person_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, victimId);
            deleteStmt.executeUpdate();
        }

        // Then add the new location assignment
        return assignPersonToLocation(victimId, locationId);
    }
}
