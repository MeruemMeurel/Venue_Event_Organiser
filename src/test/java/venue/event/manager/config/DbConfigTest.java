package venue.event.manager.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DbConfigTest {

    @Test
    void requiredValuesShouldRejectWhitespaceOnlyStrings() {
        assertThrows(IllegalStateException.class,
                () -> new DbConfig("   ", 5432, "database", "user", "password", "disable", "public"));
    }

    @Test
    void requiredValuesShouldBeTrimmed() {
        DbConfig config = new DbConfig(" localhost ", 5432, " database ", " user ", " password ",
                " disable ", " public ");

        assertEquals("jdbc:postgresql://localhost:5432/database?sslmode=disable&currentSchema=public",
                config.getJdbcUrl());
        assertEquals("user", config.getUser());
        assertEquals("password", config.getPassword());
    }

    @Test
    void constructorShouldRejectPortsOutsideTheValidRange() {
        assertThrows(IllegalStateException.class,
                () -> new DbConfig("localhost", 0, "database", "user", "password", "disable", "public"));
        assertThrows(IllegalStateException.class,
                () -> new DbConfig("localhost", 65536, "database", "user", "password", "disable", "public"));
    }

    @Test
    void constructorShouldRejectUnsupportedSslModes() {
        assertThrows(IllegalStateException.class,
                () -> new DbConfig("localhost", 5432, "database", "user", "password",
                        "disable&currentSchema=other", "public"));
    }

    @Test
    void constructorShouldRejectInvalidSchemaIdentifiers() {
        assertThrows(IllegalStateException.class,
                () -> new DbConfig("localhost", 5432, "database", "user", "password",
                        "disable", "public&sslmode=require"));
    }
}
