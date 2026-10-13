package edu.ucalgary.oop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.Test;

/** Run only with a disposable PostgreSQL database from an empty scratch directory. */
public class DatabaseAdapterLoggingIntegrationTest {
    private static final String MARKER = "syntheticQueryValue4711";

    @Test
    public void sqlFailureLogOmitsApplicationValueAndKeepsSafeCode() throws Exception {
        Path log = Paths.get("error.log");
        assertFalse("Run this test from an empty scratch directory", Files.exists(log));
        try {
            try (DatabaseAdapter db = DatabaseAdapter.createInstance();
                 Statement statement = db.getConnection().createStatement()) {
                statement.execute("CREATE TEMP TABLE items (item_id SERIAL PRIMARY KEY, item_type TEXT NOT NULL, "
                    + "description TEXT CHECK(description <> '" + MARKER + "'), quantity INTEGER, "
                    + "expiry_date DATE, room TEXT, grid TEXT)");
                assertTrue("Control insert must reach PostgreSQL",
                    db.addItem(new Item("allowed value", ItemType.WATER)));
                assertFalse("CHECK failure must preserve the failure result",
                    db.addItem(new Item(MARKER, ItemType.WATER)));
                try (ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM items")) {
                    assertTrue(rows.next());
                    assertEquals(1, rows.getInt(1));
                }
            }
            assertTrue("SQL error must still be logged", Files.exists(log));
            String contents = new String(Files.readAllBytes(log), StandardCharsets.UTF_8);
            assertFalse("Raw SQL error leaked application data into the log", contents.contains(MARKER));
            assertTrue("Keep an operation and standard SQLSTATE for diagnosis",
                contents.contains("Error adding item") && contents.contains("SQLSTATE 23514"));
        } finally {
            Files.deleteIfExists(log);
        }
    }
}
