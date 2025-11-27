package com.sahibinden.uiautomation.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory class for creating WebDriver instances.
 * Implements Factory Pattern and follows Open/Closed Principle (OCP).
 * Supports both local and remote (Grid/Selenoid) execution.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebDriverFactory {
    
    private final SeleniumConfig config;
    
    /**
     * Creates and configures a WebDriver instance based on configuration.
     * Follows DRY principle - single method for driver creation.
     */
    public WebDriver createDriver() {
        log.info("Creating WebDriver with configuration: browser={}, grid={}, headless={}", 
                config.getBrowser().getType(), 
                config.getGrid().isEnabled(),
                config.getBrowser().isHeadless());
        
        WebDriver driver;
        
        if (config.getGrid().isEnabled()) {
            driver = createRemoteDriver();
        } else {
            driver = createLocalDriver();
        }
        
        configureDriver(driver);
        return driver;
    }
    
    /**
     * Creates a local WebDriver instance.
     */
    private WebDriver createLocalDriver() {
        String browserType = config.getBrowser().getType().toLowerCase();
        
        return switch (browserType) {
            case "chrome" -> createChromeDriver();
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            default -> {
                log.warn("Unknown browser type: {}. Defaulting to Chrome.", browserType);
                yield createChromeDriver();
            }
        };
    }
    
    /**
     * Creates a Chrome driver with configured options.
     */
    private WebDriver createChromeDriver() {
        log.info("Setting up Chrome driver");
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(getChromeOptions());
    }
    
    /**
     * Creates a Firefox driver with configured options.
     */
    private WebDriver createFirefoxDriver() {
        log.info("Setting up Firefox driver");
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver(getFirefoxOptions());
    }
    
    /**
     * Creates an Edge driver with configured options.
     */
    private WebDriver createEdgeDriver() {
        log.info("Setting up Edge driver");
        WebDriverManager.edgedriver().setup();
        return new EdgeDriver(getEdgeOptions());
    }
    
    /**
     * Creates a remote WebDriver instance for Grid/Selenoid.
     */
    private WebDriver createRemoteDriver() {
        try {
            log.info("Connecting to Selenium Grid/Selenoid at: {}", config.getGrid().getHubUrl());
            URL hubUrl = new URL(config.getGrid().getHubUrl());
            
            AbstractDriverOptions<?> options = switch (config.getBrowser().getType().toLowerCase()) {
                case "firefox" -> getFirefoxOptions();
                case "edge" -> getEdgeOptions();
                default -> getChromeOptions();
            };
            
            return new RemoteWebDriver(hubUrl, options);
            
        } catch (MalformedURLException e) {
            log.error("Invalid Grid/Selenoid hub URL: {}", config.getGrid().getHubUrl(), e);
            throw new RuntimeException("Failed to create RemoteWebDriver", e);
        }
    }
    
    /**
     * Configures Chrome options including headless mode.
     */
    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        if (config.getBrowser().isHeadless()) {
            options.addArguments("--headless=new");
            log.info("Chrome headless mode enabled");
        }
        
        // Common Chrome arguments for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        return options;
    }
    
    /**
     * Configures Firefox options including headless mode.
     */
    private FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.getBrowser().isHeadless()) {
            options.addArguments("--headless");
            log.info("Firefox headless mode enabled");
        }
        
        return options;
    }
    
    /**
     * Configures Edge options including headless mode.
     */
    private EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        
        if (config.getBrowser().isHeadless()) {
            options.addArguments("--headless=new");
            log.info("Edge headless mode enabled");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        return options;
    }
    
    /**
     * Configures driver timeouts and window size.
     * Follows Single Responsibility Principle (SRP).
     */
    private void configureDriver(WebDriver driver) {
        log.info("Configuring WebDriver timeouts and window settings");
        
        // Set timeouts
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getTimeouts().getImplicitWait()));
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(config.getTimeouts().getPageLoad()));
        driver.manage().timeouts()
                .scriptTimeout(Duration.ofSeconds(config.getTimeouts().getScript()));
        
        // Set window size
        if (config.getWindow().isMaximize()) {
            driver.manage().window().maximize();
            log.info("Browser window maximized");
        } else {
            driver.manage().window()
                    .setSize(new org.openqa.selenium.Dimension(
                            config.getWindow().getWidth(),
                            config.getWindow().getHeight()));
            log.info("Browser window size set to {}x{}", 
                    config.getWindow().getWidth(), 
                    config.getWindow().getHeight());
        }
    }
}