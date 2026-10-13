package edu.ucalgary.oop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import org.junit.Test;

public class UserFacingErrorsTest {
    private static final String MARKER = "SyntheticLocationToken";

    @Test
    public void cliHidesJdbcMessage() {
        String message = UserFacingErrors.message(new SQLException("Row contains " + MARKER));
        assertEquals(UserFacingErrors.DATABASE_OPERATION_ERROR, message);
        assertFalse(message.contains(MARKER));
    }

    @Test
    public void wrappedJdbcMessageIsAlsoHidden() {
        Exception wrapper = new IllegalStateException("Failed for " + MARKER,
            new SQLException("Row contains " + MARKER));
        assertEquals(UserFacingErrors.DATABASE_OPERATION_ERROR, UserFacingErrors.message(wrapper));
    }

    @Test
    public void guiFormatterHidesJdbcMessageWithoutOpeningWindow() {
        String message = DisasterManagementGUI.errorMessageForDialog(
            new SQLException("Row contains " + MARKER));
        assertEquals(UserFacingErrors.DATABASE_OPERATION_ERROR, message);
        assertFalse(message.contains(MARKER));
    }

    @Test
    public void knownInputValidationMessageIsPreserved() {
        IllegalArgumentException invalidName = new IllegalArgumentException("Name cannot be null or empty");
        assertEquals(invalidName.getMessage(), UserFacingErrors.message(invalidName));
        assertEquals(invalidName.getMessage(), DisasterManagementGUI.errorMessageForDialog(invalidName));
    }
}
