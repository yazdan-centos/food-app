package com.mapnaom.foodapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Ensures that the configured logging directory exists when the application starts.
 */
@Component
@Slf4j
public class LoggingInitializer {

    @Value("${logging.file.path:logs}")
    private String loggingPath;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            Path logDir = Paths.get(loggingPath);
            if (Files.notExists(logDir)) {
                Files.createDirectories(logDir);
                log.info("Created logging directory: {}", logDir.toAbsolutePath());
            } else {
                log.info("Logging directory already exists: {}", logDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create logging directory '{}': {}", loggingPath, e.getMessage(), e);
        }
    }
}

