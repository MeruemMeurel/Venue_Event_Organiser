package venue.event.manager.util;

import venue.event.manager.config.TransactionManager;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/** Creates transaction managers backed by mocked JDBC resources for unit tests. */
public final class TestTransactionManagerFactory {

    private TestTransactionManagerFactory() {
    }

    /**
     * Creates a transaction manager that executes work against a mocked connection.
     * @return transaction manager isolated from an external database
     */
    public static TransactionManager create() {
        Connection connection = proxy(Connection.class, null);
        DataSource dataSource = proxy(DataSource.class, connection);
        return new TransactionManager(dataSource);
    }

    private static <T> T proxy(Class<T> type, Connection connection) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getConnection")) return connection;
                    if (method.getName().equals("isWrapperFor")) return false;
                    if (method.getName().equals("unwrap")) return null;
                    return defaultValue(method.getReturnType());
                }));
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0F;
        if (type == double.class) return 0D;
        return null;
    }
}
