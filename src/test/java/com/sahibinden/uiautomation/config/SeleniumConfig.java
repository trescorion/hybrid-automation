package com.sahibinden.uiautomation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Selenium WebDriver.
 * Follows Single Responsibility Principle (SRP) - only handles configuration data.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumConfig {
    
    private GridConfig grid = new GridConfig();
    private BrowserConfig browser = new BrowserConfig();
    private TimeoutsConfig timeouts = new TimeoutsConfig();
    private WindowConfig window = new WindowConfig();
    
    @Data
    public static class GridConfig {
        private boolean enabled = false;
        private String hubUrl = "http://localhost:4444/wd/hub";
    }
    
    @Data
    public static class BrowserConfig {
        private String type = "chrome";
        private boolean headless = false;
        private String userAgent;
    }
    
    @Data
    public static class TimeoutsConfig {
        private int implicitWait = 10;
        private int pageLoad = 30;
        private int script = 30;
    }
    
    @Data
    public static class WindowConfig {
        private boolean maximize = true;
        private int width = 1920;
        private int height = 1080;
    }
}