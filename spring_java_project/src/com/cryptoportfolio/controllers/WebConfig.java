package com.cryptoportfolio.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow specific origins (both local and dockerized front-end)
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedOrigin("http://frontend:3000");

        // Allow specific HTTP methods
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");

        // Allow all headers (or specify certain headers if needed)
        corsConfig.addAllowedHeader("*");

        // Allow credentials (cookies, headers, etc.)
        corsConfig.setAllowCredentials(true);

        // Apply this CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        // Return the configured CorsFilter
        return new CorsFilter(source);
    }
}
