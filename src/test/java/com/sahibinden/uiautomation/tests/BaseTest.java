package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.config.TestConfig;
import com.sahibinden.uiautomation.config.WebDriverFactory;
import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import com.sahibinden.uiautomation.pages.YepyPage;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

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
    
    @RegisterExtension
    final TestWatcher testWatcher = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            captureScreenshot(context.getDisplayName());
        }
    };

    public void captureScreenshot(String name) {
        if (driver != null) {
            log.info("Capturing screenshot for failed test: {}", name);
            try {
                byte[] content = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, new ByteArrayInputStream(content));
            } catch (Exception e) {
                log.error("Failed to capture screenshot", e);
            }
        }
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        log.info("╔════════════════════════════════════════════════════════════╗");
        log.info("║  Starting: {}", testInfo.getDisplayName());
        log.info("╚════════════════════════════════════════════════════════════╝");
        
        driver = webDriverFactory.createDriver();
        homePage = new SahibindenHomePage(driver, testConfig.getBaseUrl());
        yepyPage = new YepyPage(driver);
    }

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


    protected void navigateToHomePageAndVerify() {
        navigateToSahibinden();
        assertThat(isOnSahibinden())
                .as("Should be on Sahibinden.com")
                .isTrue();
    }

    protected boolean isOnSahibinden() {
        boolean result = homePage.isOnSahibindenPage();
        log.debug("Is on Sahibinden page: {}", result);
        return result;
    }

    protected String getCurrentUrl() {
        return homePage.getUrl();
    }

    protected String getBaseUrl() {
        return testConfig.getBaseUrl();
    }

    protected int getCloudflareTimeout() {
        return testConfig.getCloudflareWaitTimeout();
    }


}
