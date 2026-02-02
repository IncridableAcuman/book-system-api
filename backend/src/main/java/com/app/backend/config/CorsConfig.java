package com.app.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    public UrlBasedCorsConfigurationSource corsConfigurationSource (){
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","DELETE","PUT","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Accept","Content-Type","X-Request-With","Cookie"));
        config.setExposedHeaders(List.of("Authorization"));
        source.registerCorsConfiguration("/**",config);
        return source;
    }
}
