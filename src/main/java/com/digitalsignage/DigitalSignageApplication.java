package com.digitalsignage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application entry point for Digital Signage Backend.
 * 
 * Built with Spring Boot 3.0, Java 21, and reactive stack (WebFlux + R2DBC)
 * following DDD principles for high-load scalable microservices architecture.
 * 
 * @author Digital Signage Team
 */
@SpringBootApplication
@EnableR2dbcRepositories
@EnableScheduling
@EnableConfigurationProperties
public class DigitalSignageApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalSignageApplication.class, args);
    }
}
