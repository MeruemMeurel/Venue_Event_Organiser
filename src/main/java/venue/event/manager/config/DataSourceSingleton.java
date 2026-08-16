package venue.event.manager.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * DataSource for jdbc using singleton design pattern and Hikari
 */
public class DataSourceSingleton {

    //volatile instance to ensure to always use last updated value for all threads
    private static volatile HikariDataSource instance;


    //Constants for HikariConfig
    private static final int MAXIMUM_POOL_SIZE = 10;
    //HikariCp will try to keep at least this number of connections open
    private static final int MINIMUM_IDLE = 2;
    //Maximum time (ms) that a request will wait to get a connection from pool
    private static final int CONNECTION_TIMEOUT = 30000;
    //Maximum time (ms) that a connection can be idle in the pool before being closed
    private static final int IDLE_TIMEOUT = 600000;
    //Maximum lifetime (ms) for a connection in the pool
    private static final int MAX_LIFETIME = 1800000;
    //Maximum time for initialization before considering the pool failed
    private static final int INITIALIZATION_FAIL_TIMEOUT = 5000;
    private static final String POOL_NAME = "VenueEvent_Pool";

    /**
     * Empty constructor
     */
    private DataSourceSingleton() {}

    /**
     * Singleton implementation
     * @return DataSource instance if exists, new DataSource if not
     * @throws IllegalStateException if the database configuration cannot be loaded
     */
    public static HikariDataSource getInstance() {
        if (instance == null) {
            synchronized (DataSourceSingleton.class) {
                if (instance == null) {
                    instance = createDataSource();
                }
            }
        }
        return instance;
    }

    /**
     * Sets up configuration for HikariDataSource and creates it
     * @return new HikariDataSource
     * @throws IllegalStateException if the database configuration cannot be loaded
     */
    private static HikariDataSource createDataSource() {
        DbConfig config = DbConfig.load();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUser());
        hikariConfig.setPassword(config.getPassword());

        hikariConfig.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikariConfig.setMinimumIdle(MINIMUM_IDLE);

        hikariConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikariConfig.setIdleTimeout(IDLE_TIMEOUT);
        hikariConfig.setMaxLifetime(MAX_LIFETIME);

        hikariConfig.setPoolName(POOL_NAME);
        hikariConfig.setInitializationFailTimeout(INITIALIZATION_FAIL_TIMEOUT);

        return new HikariDataSource(hikariConfig);

    }

}
