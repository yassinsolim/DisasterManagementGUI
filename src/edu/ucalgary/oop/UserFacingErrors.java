package edu.ucalgary.oop;

import java.sql.SQLException;

/** Keeps JDBC diagnostic text out of CLI output and GUI dialogs. */
final class UserFacingErrors {
    static final String DATABASE_OPERATION_ERROR =
        "Database operation failed. Please try again or contact an administrator.";

    private UserFacingErrors() {
    }

    static String message(Throwable error) {
        Throwable cause = error;
        for (int depth = 0; cause != null && depth < 16; depth++) {
            if (cause instanceof SQLException) {
                return DATABASE_OPERATION_ERROR;
            }
            cause = cause.getCause();
        }
        // Unexpectedly deep or cyclic causes are also unsafe to display verbatim.
        return cause == null ? error.getMessage() : DATABASE_OPERATION_ERROR;
    }
}
