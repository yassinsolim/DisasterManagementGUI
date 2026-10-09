package edu.ucalgary.oop;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Test;

/**
 * Run separately against a disposable PostgreSQL database with scratch credentials.
 * The default AllTests suite does not require a database.
 */
public class DatabaseAdapterLifecycleIntegrationTest {
    @Test
    public void guiOwnedAdapterCanReconnectAfterClose() throws SQLException {
        DatabaseAdapter first;
        Connection closed;
        try (DatabaseAdapter adapter = DatabaseAdapter.createInstance()) {
            first = adapter;
            closed = adapter.getConnection();
            assertTrue(closed.isValid(2));
        }
        assertTrue(closed.isClosed());

        try (DatabaseAdapter second = DatabaseAdapter.createInstance()) {
            assertNotSame(first, second);
            assertNotSame(closed, second.getConnection());
            assertTrue(second.getConnection().isValid(2));
        }
    }

    @Test
    public void singletonAdapterCanReconnectAfterClose() throws SQLException {
        DatabaseAdapter first;
        Connection closed;
        try (DatabaseAdapter adapter = DatabaseAdapter.getInstance()) {
            first = adapter;
            closed = adapter.getConnection();
            assertTrue(closed.isValid(2));
        }
        assertTrue(closed.isClosed());

        try (DatabaseAdapter second = DatabaseAdapter.getInstance()) {
            assertNotSame(first, second);
            assertNotSame(closed, second.getConnection());
            assertTrue(second.getConnection().isValid(2));
        }
    }

    @Test
    public void singletonAdapterCanReconnectAfterDirectConnectionClose() throws SQLException {
        DatabaseAdapter first = DatabaseAdapter.getInstance();
        Connection closed = first.getConnection();
        closed.close();
        assertTrue(closed.isClosed());

        try (DatabaseAdapter second = DatabaseAdapter.getInstance()) {
            assertNotSame(first, second);
            assertNotSame(closed, second.getConnection());
            assertTrue(second.getConnection().isValid(2));
        }
    }
}
