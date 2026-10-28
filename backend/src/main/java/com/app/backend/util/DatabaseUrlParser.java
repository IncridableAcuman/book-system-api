package com.app.backend.util;

import java.net.URI;

public class DatabaseUrlParser {

    private DatabaseUrlParser() {}

    public static void configureDatabaseProperties() {
        String rawUrl = getEnvOrProperty("DB_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = getEnvOrProperty("DATABASE_URL");
        }

        if (rawUrl == null || rawUrl.isBlank()) {
            return;
        }

        try {
            // Strip jdbc: prefix for URI parsing if present
            String cleanUriStr = rawUrl.startsWith("jdbc:") ? rawUrl.substring(5) : rawUrl;
            URI uri = URI.create(cleanUriStr);

            String userInfo = uri.getUserInfo();
            if (userInfo != null && !userInfo.isBlank()) {
                String[] parts = userInfo.split(":", 2);
                if (parts.length > 0 && !parts[0].isBlank()) {
                    System.setProperty("DB_USER", parts[0]);
                }
                if (parts.length > 1 && !parts[1].isBlank()) {
                    System.setProperty("DB_PASS", parts[1]);
                }
            }

            String host = uri.getHost();
            if (host == null) {
                // If URI doesn't have host (malformed), fallback to prefix replacement
                applyFallback(rawUrl);
                return;
            }

            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            String query = uri.getQuery();

            StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://");
            jdbcUrl.append(host).append(":").append(port);

            if (path != null && !path.isBlank()) {
                jdbcUrl.append(path);
            } else {
                jdbcUrl.append("/neondb");
            }

            boolean hasQuery = query != null && !query.isBlank();
            boolean hasSslMode = hasQuery && query.contains("sslmode=");

            if (hasQuery) {
                jdbcUrl.append("?").append(query);
            }

            if ((host.contains("neon.tech") || host.contains("aws.neon")) && !hasSslMode) {
                jdbcUrl.append(hasQuery ? "&" : "?").append("sslmode=require");
            }

            System.setProperty("DB_URL", jdbcUrl.toString());
        } catch (Exception e) {
            applyFallback(rawUrl);
        }
    }

    private static void applyFallback(String rawUrl) {
        if (rawUrl.startsWith("postgres://")) {
            rawUrl = "jdbc:postgresql://" + rawUrl.substring("postgres://".length());
        } else if (rawUrl.startsWith("postgresql://")) {
            rawUrl = "jdbc:postgresql://" + rawUrl.substring("postgresql://".length());
        }
        System.setProperty("DB_URL", rawUrl);
    }

    private static String getEnvOrProperty(String key) {
        String prop = System.getProperty(key);
        if (prop != null && !prop.isBlank()) {
            return prop;
        }
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return null;
    }
}
