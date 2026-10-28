package com.app.backend.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

class NeonConnectionTest {

    static boolean isNeonConfigured() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String url = dotenv.get("DB_URL");
        return url != null && url.contains("neon.tech");
    }

    @Test
    @EnabledIf("isNeonConfigured")
    @DisplayName("Should successfully connect to Neon PostgreSQL using .env")
    void testNeonConnection() throws Exception {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        for (DotenvEntry entry : dotenv.entries()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }

        DatabaseUrlParser.configureDatabaseProperties();

        String dbUrl = System.getProperty("DB_URL");
        String dbUser = System.getProperty("DB_USER");
        String dbPass = System.getProperty("DB_PASS");

        assertThat(dbUrl).startsWith("jdbc:postgresql://");
        assertThat(dbUser).isEqualTo("neondb_owner");

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version(), current_database(), current_user")) {

            assertThat(rs.next()).isTrue();
            System.out.println("Neon DB Connection SUCCESS!");
            System.out.println("Database: " + rs.getString(2));
            System.out.println("User: " + rs.getString(3));
            System.out.println("Version: " + rs.getString(1));
        }
    }
}
