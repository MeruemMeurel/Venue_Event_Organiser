package Venue_Event_Manager.config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Handles database config and creates jdbc url
 */
public final class DbConfig {
    private final String host;
    private final int port;
    private final String dbName;
    private final String user;
    private final String password;
    private final String sslMode;   // disable, require, verify-full...
    private final String schema;

    /**
     * DBConfig constructor
     * @param host required Non Blank
     * @param port database port in the range 1-65535
     * @param dbName required Non Blank
     * @param user required Non Blank
     * @param password required Non Blank
     * @param sslMode can be null
     * @param schema can be null
     * @throws IllegalStateException if a required configuration value is missing
     */
    public DbConfig(String host, int port, String dbName, String user, String password, String sslMode,
                    String schema){
        this.host = requireNonBlank(host, "db.host/DB_HOST");
        this.port = port;
        this.dbName = requireNonBlank(dbName, "db.name/DB_NAME");
        this.user = requireNonBlank(user, "db.user/DB_USER");
        this.password = requireNonBlank(password, "db.password/DB_PASSWORD");
        this.sslMode = (sslMode == null || sslMode.isBlank()) ? "disable" : sslMode.trim();
        this.schema = (schema == null || schema.isBlank()) ? "public" : schema.trim();
    }



    /**
     * Load Db config from application.properties file in project root
     * @return new DbConfig object with configs from application.properties
     * @throws IllegalStateException if the properties cannot be read or contain invalid values
     */
    public static DbConfig load() {
        Properties props = loadPropertiesFromClasspath("application.properties");

        String host = envOrProp("DB_HOST", props, "db.host");
        String portStr = envOrProp("DB_PORT", props, "db.port");
        String name = envOrProp("DB_NAME", props, "db.name");
        String user = envOrProp("DB_USER", props, "db.user");
        String password = envOrProp("DB_PASSWORD", props, "db.password");
        String sslMode = envOrProp("DB_SSLMODE", props, "db.sslMode");
        String schema = envOrProp("DB_SCHEMA", props, "db.schema");

        int port = parsePort(portStr);

        return new DbConfig(host, port, name, user, password, sslMode, schema);

    }

    /**
     * Get url with correct configs for jdbc
     * @return jdbc url
     */
    public String getJdbcUrl(){
        return String.format("jdbc:postgresql://%s:%d/%s?sslmode=%s&currentSchema=%s",
                host, port, dbName, urlEncode(sslMode), urlEncode(schema));
    }

    //getters
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }

    //-------------------Helpers---------------------

    /**
     * Create Properties object from resource
     * @param resourceName file containing properties
     * @return properties object
     * @throws IllegalStateException if the resource cannot be read
     */
    private static Properties loadPropertiesFromClasspath(String resourceName) {
        Properties props = new Properties();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream(resourceName)){
            if (in != null) props.load(in);
        }catch (IOException e) {
            throw new IllegalStateException("Impossible to read "+resourceName,e);
        }
        return props;
    }

    /**
     * Parse port number from string, if null defaults to 5432
     * @param portStr
     * @return port number parsed from string
     * @throws IllegalStateException if port number is not valid
     */
    private static int parsePort(String portStr) {
        if(portStr == null || portStr.isBlank()) return 5432;//defailt port
        try{
            int port = Integer.parseInt(portStr.trim());
            if(port < 1 || port > 65535) throw new NumberFormatException();
            return port;
        }catch (NumberFormatException e){
            throw new IllegalStateException("Invalid port number "+portStr);
        }
    }

    /**
     * Encodes url from string
     * @param s string to encode into url
     * @return encoded url from string
     */
    private static String urlEncode(String s){
        return Objects.requireNonNullElse(s, "").replace(" ", "%20");
    }

    /**
     * Checks if required label is blank
     * @param string
     * @param label
     * @throws IllegalStateException
     * @return string trimmed without spaces
     */
    private static String requireNonBlank(String string, String label) {
        if(string == null || string.isEmpty()) {
            throw new IllegalStateException("Missing config: " + label);
        }
        return string.trim();
    }

    /**
     * Get value from environment variable or properties file
     * @param envKey environment variable key
     * @param props properties object
     * @param propKey properties key
     * @return value from environment variable or properties file
     */
    private static String envOrProp(String envKey, Properties props, String propKey) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env.trim();
        return props.getProperty(propKey);
    }

}
