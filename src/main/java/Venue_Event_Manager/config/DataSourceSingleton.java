package Venue_Event_Manager.config;

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
    private static final int maximumPoolSize = 10;
    //HikariCp will try to keep at least this number of connections open
    private static final int minimumIdle = 2;
    //Maximum time (ms) that a request will wait to get a connection from pool
    private static final int connectionTimeout = 30000;
    //Maximum time (ms) that a connection can be idle in the pool before being closed
    private static final int idleTimeout = 600000;
    //Maximum lifetime (ms) for a connection in the pool
    private static final int maxLifetime = 1800000;
    //Maximum time for initialization before considering the pool failed
    private static final int initializationFailTimeout = 5000;
    private static final String poolName = "VenueEvent_Pool";

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

        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);

        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setMaxLifetime(maxLifetime);

        hikariConfig.setPoolName(poolName);
        hikariConfig.setInitializationFailTimeout(initializationFailTimeout);

        return new HikariDataSource(hikariConfig);

    }

}
