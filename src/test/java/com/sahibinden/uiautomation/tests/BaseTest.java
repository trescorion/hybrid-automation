package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.config.TestConfig;
import com.sahibinden.uiautomation.config.WebDriverFactory;
import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import com.sahibinden.uiautomation.pages.YepyPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

/**
 * Base test class for all UI tests.
 * Implements Template Method pattern - provides common setup/teardown.
 * Follows DRY principle - all tests inherit common functionality.
 * Includes automatic Cloudflare verification handling.
 */
@Slf4j
@SpringBootTest
public abstract class BaseTest {
    
    @Autowired
    protected WebDriverFactory webDriverFactory;
    
    @Autowired
    protected TestConfig testConfig;
    
    protected WebDriver driver;
    protected SahibindenHomePage homePage;
    protected YepyPage yepyPage;
    
    private static final int COOKIE_BANNER_WAIT_TIMEOUT = 5; // Shorter timeout for optional element
    
    private static final By COOKIE_ACCEPT_ALL = By.id("onetrust-accept-btn-handler");
    
    /**
     * Sets up WebDriver before each test.
     * Automatically logs test start information.
     *
     * @param testInfo JUnit5 test information
     */
    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("╔════════════════════════════════════════════════════════════╗");
        log.info("║  Starting: {}", testInfo.getDisplayName());
        log.info("╚════════════════════════════════════════════════════════════╝");
        
        driver = webDriverFactory.createDriver();
        homePage = new SahibindenHomePage(driver, testConfig.getBaseUrl());
        yepyPage = new YepyPage(driver);
    }
    
    /**
     * Tears down WebDriver after each test.
     * Automatically logs test completion status.
     *
     * @param testInfo JUnit5 test information
     */
    @AfterEach
    public void tearDown(TestInfo testInfo) {
        // Log test completion (JUnit will show if passed/failed)
        log.info("╔════════════════════════════════════════════════════════════╗");
        log.info("║  Completed: {}", testInfo.getDisplayName());
        log.info("║  Final URL: {}", getCurrentUrl());
        log.info("╚════════════════════════════════════════════════════════════╝");
        
        if (driver != null) {
            try {
                driver.quit();
                log.debug("WebDriver closed successfully");
            } catch (Exception e) {
                log.error("Error closing WebDriver", e);
            }
        }
    }
    
    /**
     * Navigates to Sahibinden.com with automatic handling of:
     * 1. Cloudflare verification
     * 2. Cookie banner dismissal
     * 3. Page load verification
     *
     * Use this method instead of directly calling homePage.open()
     *
     * @return SahibindenHomePage for method chaining
     */
    protected SahibindenHomePage navigateToSahibinden() {
        log.info("Navigating to Sahibinden.com with automatic overlay handling...");
        
        // Step 1: Open the page
        homePage.open();
        
        // Step 2: Check for Cloudflare challenge
        if (homePage.isCloudflareChallenge()) {
            log.warn("╔════════════════════════════════════════════════════════════╗");
            log.warn("║  CLOUDFLARE CHALLENGE DETECTED                             ║");
            log.warn("║  Waiting for automatic/manual verification...              ║");
            log.warn("║  Timeout: {} seconds                                       ║", getCloudflareTimeout());
            log.warn("╚════════════════════════════════════════════════════════════╝");
            
            // Wait for verification (automatic or manual)
            boolean verified = homePage.waitForCloudflareVerification(getCloudflareTimeout());
            
            if (!verified) {
                log.error("Cloudflare verification failed or timed out!");
                throw new RuntimeException("Failed to bypass Cloudflare verification");
            }
            
            log.info("✓ Cloudflare verification completed successfully");
        } else {
            log.info("✓ No Cloudflare challenge detected");
        }
        
        // Step 3: Wait for page to fully load
        boolean pageLoaded = homePage.waitForPageLoad(30);
        if (!pageLoaded) {
            log.error("Page failed to load within timeout");
            throw new RuntimeException("Sahibinden.com page load timeout");
        }
        
        // Step 4: Dismiss cookie banner (appears on home page, one-time)
        dismissCookieBanner();
        
        log.info("✓ Successfully navigated to Sahibinden.com");
        return homePage;
    }
    
    /**
     * Generic method to wait for an element to be clickable using explicit wait.
     * This is a reusable method for any element that needs to be clicked.
     * 
     * @param locator By locator for the element
     * @param elementName descriptive name for logging
     * @param timeoutSeconds timeout in seconds
     * @return WebElement if found and clickable, null if timeout occurs
     */
    protected WebElement waitForElementClickable(By locator, String elementName, int timeoutSeconds) {
        log.debug("Waiting for element '{}' to be clickable (timeout: {}s)...", elementName, timeoutSeconds);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            log.debug("✓ Element '{}' is now clickable", elementName);
            return element;
        } catch (TimeoutException e) {
            log.debug("Element '{}' not clickable within {} seconds: {}", elementName, timeoutSeconds, e.getMessage());
            return null;
        }
    }

    
    /**
     * Generic method to scroll the page.
     * Useful for bringing elements into view before interaction.
     *
     * @param scrollTo "top", "bottom", or pixel value as string (e.g., "500")
     */
    protected void scrollPage(String scrollTo) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        switch (scrollTo.toLowerCase()) {
            case "top":
                js.executeScript("window.scrollTo(0, 0);");
                log.debug("Scrolled to top of page");
                break;
            case "bottom":
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                log.debug("Scrolled to bottom of page");
                break;
            default:
                try {
                    int pixels = Integer.parseInt(scrollTo);
                    js.executeScript("window.scrollTo(0, " + pixels + ");");
                    log.debug("Scrolled {} pixels", pixels);
                } catch (NumberFormatException e) {
                    log.warn("Invalid scroll value: {}. Use 'top', 'bottom', or pixel number.", scrollTo);
                }
        }
    }
    
    /**
     * Dismisses the cookie/privacy banner if present.
     * Cookie banner typically appears at the bottom of the page, so we scroll first.
     * Uses generic reusable method: waitForElementClickable.
     * Cookie banner appears on home page and should be dismissed after each setup.
     */
    private void dismissCookieBanner() {
        log.debug("Checking for cookie banner...");

        scrollPage("bottom");
        
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        scrollPage("top");
        
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        WebElement cookieButton = waitForElementClickable(
            COOKIE_ACCEPT_ALL, 
            "Cookie Accept Button",
            COOKIE_BANNER_WAIT_TIMEOUT
        );
        
        if (cookieButton != null) {
            cookieButton.click();
            log.info("✓ Cookie banner dismissed successfully");
        } else {
            log.debug("Cookie banner not found (may already be dismissed or not present)");
        }
    }
    
    /**
     * Verifies that we are on the actual Sahibinden.com page (not Cloudflare or error page).
     *
     * @return true if on Sahibinden.com
     */
    protected boolean isOnSahibinden() {
        boolean result = homePage.isOnSahibindenPage();
        log.debug("Is on Sahibinden page: {}", result);
        return result;
    }
    
    /**
     * Gets the current page URL.
     *
     * @return current URL
     */
    protected String getCurrentUrl() {
        return homePage.getUrl();
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