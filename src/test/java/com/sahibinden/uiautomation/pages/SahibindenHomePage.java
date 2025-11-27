package com.sahibinden.uiautomation.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Page Object for Sahibinden.com home page.
 * Implements Page Object Model pattern.
 * Follows Single Responsibility Principle (SRP) - only handles home page interactions.
 */
@Slf4j
public class SahibindenHomePage extends BasePage {
    
    public static final By YEPY_LINK = By.id("yepy-link-category-tree");
    
    private final String baseUrl;
    
    /**
     * Constructor.
     * 
     * @param driver WebDriver instance
     * @param baseUrl base URL of the application
     */
    public SahibindenHomePage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
        log.info("Initialized SahibindenHomePage with base URL: {}", baseUrl);
    }
    
    /**
     * Opens the Sahibinden home page.
     * 
     * @return this page object for method chaining
     */
    public SahibindenHomePage open() {
        log.info("Opening Sahibinden home page: {}", baseUrl);
        navigateTo(baseUrl);
        return this;
    }
    
    /**
     * Waits for the page to be fully loaded.
     * This is crucial for Cloudflare verification scenarios.
     * 
     * @param timeoutInSeconds maximum wait time in seconds
     * @return true if page loaded successfully
     */
    public boolean waitForPageLoad(int timeoutInSeconds) {
        log.info("Waiting for page to load (max {} seconds)", timeoutInSeconds);
        try {
            // Create a custom wait for longer timeouts (for Cloudflare verification)
            createWait(timeoutInSeconds).until(driver -> {
                String currentUrl = getCurrentUrl();
                log.debug("Current URL: {}", currentUrl);
                // Check if we're on the actual Sahibinden domain (not Cloudflare)
                return currentUrl.contains("sahibinden.com") && 
                       !currentUrl.contains("challenge") &&
                       !currentUrl.contains("waiting");
            });
            log.info("Page loaded successfully. Current URL: {}", getCurrentUrl());
            return true;
        } catch (Exception e) {
            log.error("Page failed to load within {} seconds. Current URL: {}", 
                     timeoutInSeconds, getCurrentUrl(), e);
            return false;
        }
    }
    
    /**
     * Verifies that the page is the actual Sahibinden.com page.
     * This confirms we're past Cloudflare verification.
     * 
     * @return true if on actual Sahibinden page
     */
    public boolean isOnSahibindenPage() {
        String currentUrl = getCurrentUrl();
        log.info("Verifying Sahibinden page. Current URL: {}", currentUrl);
        
        boolean urlCheck = currentUrl.contains("sahibinden.com") && 
                          !currentUrl.contains("challenge");
        
        if (!urlCheck) {
            log.warn("URL check failed. Still on Cloudflare or other page: {}", currentUrl);
            return false;
        }
        
        // Additional verification: check for Sahibinden-specific elements
    //    try {
    //        boolean logoDisplayed = isDisplayed(sahibindenLogo);
    //        log.info("Sahibinden logo displayed: {}", logoDisplayed);
    //        return logoDisplayed;
    //    } catch (Exception e) {
    //        log.warn("Could not verify Sahibinden logo: {}", e.getMessage());
    //        return urlCheck; // Fall back to URL check
    //    }

        return urlCheck;
    }
    
    /**
     * Gets the current page URL.
     * Useful for verification and debugging.
     * 
     * @return current URL
     */
    public String getUrl() {
        String url = getCurrentUrl();
        log.debug("Current page URL: {}", url);
        return url;
    }
    
    /**
     * Checks if Cloudflare challenge is present.
     * 
     * @return true if Cloudflare challenge is detected
     */
    public boolean isCloudflareChallenge() {
        String currentUrl = getCurrentUrl();
        String pageTitle = getPageTitle();
        
        boolean isChallenge = currentUrl.contains("challenge") || 
                             currentUrl.contains("waiting") ||
                             pageTitle.toLowerCase().contains("cloudflare") ||
                             pageTitle.toLowerCase().contains("just a moment");
        
        log.info("Cloudflare challenge detected: {}. URL: {}, Title: {}", 
                isChallenge, currentUrl, pageTitle);
        
        return isChallenge;
    }
    
    /**
     * Waits for manual Cloudflare verification by user.
     * Keeps checking if we've moved past Cloudflare.
     * 
     * @param timeoutInSeconds maximum wait time
     * @return true if verification completed
     */
    public boolean waitForCloudflareVerification(int timeoutInSeconds) {
        log.warn("Cloudflare verification detected. Waiting for manual user interaction...");
        log.info("USER ACTION REQUIRED: Please complete the Cloudflare verification manually");
        
        try {
            createWait(timeoutInSeconds).until(driver -> {
                boolean stillOnCloudflare = isCloudflareChallenge();
                if (stillOnCloudflare) {
                    log.debug("Still on Cloudflare challenge page, waiting...");
                }
                return !stillOnCloudflare;
            });
            log.info("Cloudflare verification completed successfully!");
            return true;
        } catch (Exception e) {
            log.error("Cloudflare verification timeout after {} seconds", timeoutInSeconds);
            return false;
        }
    }
    
    /**
     * Generic method to click any category link by locator.
     * Uses strict clicking approach from BasePage.
     *
     * Example usage:
     *   homePage.clickCategoryLink(SahibindenHomePage.YEPY_LINK, "Yepy");
     *   homePage.clickCategoryLink(By.linkText("Emlak"), "Emlak");
     *
     * @param locator By locator for the element
     * @param linkName descriptive name for logging
     * @throws AssertionError if click fails (UI problem)
     */
    public void clickCategoryLink(By locator, String linkName) {
        clickElement(locator, linkName + " category link");
    }
    
    /**
     * Generic method to check if a category link is displayed.
     *
     * @param locator By locator for the element
     * @param linkName descriptive name for logging
     * @return true if link is visible
     */
    public boolean isCategoryLinkDisplayed(By locator, String linkName) {
        return isElementDisplayed(locator, linkName + " category link");
    }
    
}