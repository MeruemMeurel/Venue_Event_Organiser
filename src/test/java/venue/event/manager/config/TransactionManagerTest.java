package venue.event.manager.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void readOnlyCleanupShouldRestoreBothConnectionProperties() {
        AtomicInteger autoCommitChanges = new AtomicInteger();
        AtomicBoolean readOnlyRestored = new AtomicBoolean();

        Connection connection = (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "getAutoCommit" -> true;
                    case "isReadOnly" -> false;
                    case "setAutoCommit" -> {
                        if (autoCommitChanges.incrementAndGet() == 2) {
                            throw new SQLException("auto-commit restore failed");
                        }
                        yield null;
                    }
                    case "setReadOnly" -> {
                        if (Boolean.FALSE.equals(arguments[0])) readOnlyRestored.set(true);
                        yield null;
                    }
                    case "rollback", "close" -> null;
                    case "isClosed" -> false;
                    default -> throw new UnsupportedOperationException(method.getName());
                });

        DataSource dataSource = (DataSource) Proxy.newProxyInstance(
                DataSource.class.getClassLoader(),
                new Class<?>[]{DataSource.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getConnection")) return connection;
                    throw new UnsupportedOperationException(method.getName());
                });

        TransactionManager manager = new TransactionManager(dataSource);

        assertThrows(TransactionException.class, () -> manager.inReadOnly(conn -> "result"));
        assertTrue(readOnlyRestored.get());
    }
}
