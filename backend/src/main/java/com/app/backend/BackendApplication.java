package com.app.backend;

import com.app.backend.util.DatabaseUrlParser;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BackendApplication {

	public static void main(String[] args) {
		// Load .env if present (for local dev), ignore if absent (for Render / Docker)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		for (DotenvEntry entry : dotenv.entries()){
			if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
		}

		// Normalize DB_URL / DATABASE_URL for Neon / cloud Postgres
		DatabaseUrlParser.configureDatabaseProperties();

		// Normalize REDIS_URL for Upstash / Render Redis if provided
		configureRedisUrl();

		SpringApplication.run(BackendApplication.class, args);
	}

	private static void configureRedisUrl() {
		String redisUrl = System.getProperty("REDIS_URL");
		if (redisUrl == null || redisUrl.isBlank()) {
			redisUrl = System.getenv("REDIS_URL");
		}
		if (redisUrl != null && !redisUrl.isBlank()) {
			System.setProperty("spring.data.redis.url", redisUrl);
		}
	}

}
