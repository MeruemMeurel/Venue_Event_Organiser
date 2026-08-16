package venue.event.manager.config;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void readOnlyCleanupShouldRestoreBothConnectionProperties() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.isReadOnly()).thenReturn(false);
        doNothing().doThrow(new SQLException("auto-commit restore failed"))
                .when(connection).setAutoCommit(org.mockito.ArgumentMatchers.anyBoolean());

        TransactionManager manager = new TransactionManager(dataSource);

        assertThrows(TransactionException.class, () -> manager.inReadOnly(conn -> "result"));
        verify(connection).setReadOnly(false);
    }
}
