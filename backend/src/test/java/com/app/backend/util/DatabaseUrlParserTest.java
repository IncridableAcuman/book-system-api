package com.app.backend.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseUrlParserTest {

    @BeforeEach
    @AfterEach
    void cleanProperties() {
        System.clearProperty("DB_URL");
        System.clearProperty("DB_USER");
        System.clearProperty("DB_PASS");
        System.clearProperty("DATABASE_URL");
    }

    @Test
    @DisplayName("Should parse Neon postgresql URL with credentials and sslmode")
    void testNeonUrlWithCredentials() {
        System.setProperty("DB_URL", "postgresql://alex:supersecret@ep-cool-fog-123456.us-east-2.aws.neon.tech/neondb?sslmode=require");

        DatabaseUrlParser.configureDatabaseProperties();

        assertThat(System.getProperty("DB_URL"))
                .isEqualTo("jdbc:postgresql://ep-cool-fog-123456.us-east-2.aws.neon.tech:5432/neondb?sslmode=require");
        assertThat(System.getProperty("DB_USER")).isEqualTo("alex");
        assertThat(System.getProperty("DB_PASS")).isEqualTo("supersecret");
    }

    @Test
    @DisplayName("Should automatically append sslmode=require if Neon host is detected without sslmode")
    void testNeonUrlWithoutSslMode() {
        System.setProperty("DB_URL", "postgresql://owner:pass123@ep-sample-123.eu-central-1.aws.neon.tech/neondb");

        DatabaseUrlParser.configureDatabaseProperties();

        assertThat(System.getProperty("DB_URL"))
                .isEqualTo("jdbc:postgresql://ep-sample-123.eu-central-1.aws.neon.tech:5432/neondb?sslmode=require");
        assertThat(System.getProperty("DB_USER")).isEqualTo("owner");
        assertThat(System.getProperty("DB_PASS")).isEqualTo("pass123");
    }

    @Test
    @DisplayName("Should handle standard jdbc:postgresql URL cleanly without modifying existing user/pass")
    void testStandardJdbcUrl() {
        System.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/book_system");
        System.setProperty("DB_USER", "admin");
        System.setProperty("DB_PASS", "admin");

        DatabaseUrlParser.configureDatabaseProperties();

        assertThat(System.getProperty("DB_URL")).isEqualTo("jdbc:postgresql://localhost:5432/book_system");
        assertThat(System.getProperty("DB_USER")).isEqualTo("admin");
        assertThat(System.getProperty("DB_PASS")).isEqualTo("admin");
    }

    @Test
    @DisplayName("Should fallback from DATABASE_URL if DB_URL is not set")
    void testDatabaseUrlFallback() {
        System.setProperty("DATABASE_URL", "postgres://renderuser:renderpass@dpg-xxx.frankfurt.render.com/mydb");

        DatabaseUrlParser.configureDatabaseProperties();

        assertThat(System.getProperty("DB_URL"))
                .isEqualTo("jdbc:postgresql://dpg-xxx.frankfurt.render.com:5432/mydb");
        assertThat(System.getProperty("DB_USER")).isEqualTo("renderuser");
        assertThat(System.getProperty("DB_PASS")).isEqualTo("renderpass");
    }
}
