package Venue_Event_Manager.config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class DbConfig {
    public final String host;
    private final int port;
    private final String dbName;
    private final String user;
    private final String password;
    private final String sslMode;   // disable, require, verify-full...
    private final String schema;

    /**
     * DBConfig constructor
     * @param host required Non Blank
     * @param port
     * @param dbName required Non Blank
     * @param user required Non Blank
     * @param password required Non Blank
     * @param sslMode can be null
     * @param schema can be null
     * @throws IOException
     */
    public DbConfig(String host, int port, String dbName, String user, String password, String sslMode,
                    String schema) throws IOException {
        this.host = requireNonBlank(host, "db.host/DB_HOST");
        this.port = port;
        this.dbName = requireNonBlank(dbName, "db.name/DB_NAME");
        this.user = requireNonBlank(user, "db.user/DB_USER");
        this.password = requireNonBlank(password, "db.password/DB_PASSWORD");
        this.sslMode = (sslMode == null || sslMode.isBlank()) ? "disable" : sslMode.trim();
        this.schema = (schema == null || schema.isBlank()) ? "public" : schema.trim();
    }

    /**
     * DbConfig constructor without sslMode and schema
     * @param host
     * @param port
     * @param dbName
     * @param user
     * @param password
     * @throws IOException
     */
    public DbConfig(String host, int port, String dbName, String user, String password) throws IOException {
        this.host = requireNonBlank(host, "db.host/DB_HOST");
        this.port = port;
        this.dbName = requireNonBlank(dbName, "db.name/DB_NAME");
        this.user = requireNonBlank(user, "db.user/DB_USER");
        this.password = requireNonBlank(password, "db.password/DB_PASSWORD");
    }

    /**
     * Load Db config from application.properties file in project root
     * @return new DbConfig object with configs from application.properties
     * @throws IOException
     */
    public static DbConfig load() throws IOException {
        Properties props = loadPropertiesFromClasspath("application.properties");

        String host = props.getProperty("db.host");
        String portStr = props.getProperty("db.port");
        String Name = props.getProperty("db.name");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        String sslMode = props.getProperty("db.sslMode");
        String schema = props.getProperty("db.schema");

        //TODO add env (environmental variables) if so decided

        int port = parsePort(portStr);

        return new DbConfig(host, port, Name, user, password, sslMode, schema);

    }

    //Helpers---------------------

    /**
     * Create Properties object from resource
     * @param resourceName file containing properties
     * @return properties object
     * @throws IOException
     */
    private static Properties loadPropertiesFromClasspath(String resourceName) throws IOException {
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
    private static int parsePort(String portStr) throws IllegalStateException {
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
     * @exception IllegalStateException
     * @return string trimmed without spaces
     */
    private static String requireNonBlank(String string, String label) throws IllegalArgumentException {
        if(string == null || string.isEmpty()) {
            throw new IllegalStateException("Missing config: " + label);
        }
        return string.trim();
    }

}
