package com.sahibinden.uiautomation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Test-specific configuration properties.
 * Follows Single Responsibility Principle (SRP).
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "test")
public class TestConfig {
    
    private String baseUrl = "https://www.sahibinden.com";
    private int cloudflareWaitTimeout = 30;
}