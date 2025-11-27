package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.config.TestConfig;
import com.sahibinden.uiautomation.config.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base test class for all UI tests.
 * Implements Template Method pattern - provides common setup/teardown.
 * Follows DRY principle - all tests inherit common functionality.
 */
@Slf4j
@SpringBootTest
public abstract class BaseTest {
    
    @Autowired
    protected WebDriverFactory webDriverFactory;
    
    @Autowired
    protected TestConfig testConfig;
    
    protected WebDriver driver;
    
    /**
     * Sets up WebDriver before each test.
     * Follows Open/Closed Principle (OCP) - tests can override if needed.
     */
    @BeforeEach
    public void setUp() {
        log.info("=== Starting test setup ===");
        driver = webDriverFactory.createDriver();
        log.info("WebDriver initialized successfully");
    }
    
    /**
     * Tears down WebDriver after each test.
     * Ensures proper cleanup even if test fails.
     */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            log.info("=== Starting test teardown ===");
            try {
                driver.quit();
                log.info("WebDriver closed successfully");
            } catch (Exception e) {
                log.error("Error closing WebDriver", e);
            }
        }
    }
    
    /**
     * Gets the base URL for tests.
     * 
     * @return base URL
     */
    protected String getBaseUrl() {
        return testConfig.getBaseUrl();
    }
    
    /**
     * Gets the Cloudflare wait timeout.
     * 
     * @return timeout in seconds
     */
    protected int getCloudflareTimeout() {
        return testConfig.getCloudflareWaitTimeout();
    }
}