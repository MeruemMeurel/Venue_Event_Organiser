package Venue_Event_Manager.config;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionManagerTest {

    @Test
    void cleanupFailureShouldBeSuppressedByTheOriginalFailure() {
        RuntimeException original = new RuntimeException("work failed");
        SQLException cleanup = new SQLException("restore failed");

        TransactionManager.handleCleanupFailure("cleanup",cleanup,original);

        assertEquals(1,original.getSuppressed().length);
        assertEquals("restore failed",original.getSuppressed()[0].getMessage());
    }

    @Test
    void cleanupFailureWithoutAnOriginalFailureShouldBeReported() {
        TransactionException thrown = assertThrows(TransactionException.class,
                () -> TransactionManager.handleCleanupFailure(
                        "Couldn't restore auto-commit",new SQLException("restore failed"),null));

        assertEquals("Couldn't restore auto-commit",thrown.getMessage());
    }
}
