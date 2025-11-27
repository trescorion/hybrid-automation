package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Sahibinden.com Cloudflare verification scenario.
 * 
 * Test Scenario:
 * 1. Navigate to sahibinden.com
 * 2. Wait for user to manually complete Cloudflare verification
 * 3. Verify successful navigation to the actual Sahibinden.com page
 * 
 * Implements best practices:
 * - KISS: Simple, straightforward test logic
 * - DRY: Reuses Page Object methods
 * - SOLID: Single Responsibility - only tests Cloudflare verification
 * - Page Object Model: Uses SahibindenHomePage
 */
@Slf4j
@DisplayName("Sahibinden.com Cloudflare Verification Tests")
public class SahibindenCloudflareTest extends BaseTest {
    
    @Test
    @BeforeEach
    @DisplayName("Test #1: Navigate to Sahibinden.com and verify Cloudflare bypass")
    public void testCloudflareVerificationAndPageLoad() {
        log.info("========================================");
        log.info("Starting Test #1: Cloudflare Verification");
        log.info("========================================");
        
        // Step 1: Create page object
        SahibindenHomePage homePage = new SahibindenHomePage(driver, getBaseUrl());
        
        // Step 2: Navigate to Sahibinden.com
        log.info("STEP 1: Navigating to Sahibinden.com...");
        homePage.open();
        
        // Step 3: Check if Cloudflare challenge appears
        log.info("STEP 2: Checking for Cloudflare challenge...");
        if (homePage.isCloudflareChallenge()) {
            log.warn("╔════════════════════════════════════════════════════════════╗");
            log.warn("║  CLOUDFLARE CHALLENGE DETECTED                             ║");
            log.warn("║  USER ACTION REQUIRED:                                     ║");
            log.warn("║  Please complete the Cloudflare verification manually      ║");
            log.warn("║  Waiting for up to {} seconds...                           ║", getCloudflareTimeout());
            log.warn("╚════════════════════════════════════════════════════════════╝");
            
            // Wait for user to complete Cloudflare verification
            boolean verificationCompleted = homePage.waitForCloudflareVerification(getCloudflareTimeout());
            
            assertThat(verificationCompleted)
                .as("Cloudflare verification should be completed within timeout")
                .isTrue();
            
            log.info("✓ Cloudflare verification completed successfully");
        } else {
            log.info("✓ No Cloudflare challenge detected - proceeding directly");
        }
        
        // Step 4: Wait for page to fully load
        log.info("STEP 3: Waiting for Sahibinden.com page to load...");
        boolean pageLoaded = homePage.waitForPageLoad(30);
        
        assertThat(pageLoaded)
            .as("Sahibinden.com page should load successfully")
            .isTrue();
        
        log.info("✓ Page loaded successfully");
        
        // Step 5: Verify we're on the actual Sahibinden.com page
        log.info("STEP 4: Verifying navigation to actual Sahibinden.com page...");
        boolean onSahibindenPage = homePage.isOnSahibindenPage();
        
        assertThat(onSahibindenPage)
            .as("Should be on actual Sahibinden.com page after Cloudflare verification")
            .isTrue();
        
        String currentUrl = homePage.getUrl();
        log.info("✓ Successfully navigated to Sahibinden.com");
        log.info("✓ Current URL: {}", currentUrl);
        
        // Step 6: Additional verification - URL should contain sahibinden.com
        assertThat(currentUrl)
            .as("URL should contain sahibinden.com domain")
            .contains("sahibinden.com");
        
        assertThat(currentUrl)
            .as("URL should not contain Cloudflare challenge indicators")
            .doesNotContain("challenge")
            .doesNotContain("waiting");
        
        log.info("========================================");
        log.info("✓ Test #1 PASSED: Cloudflare verification successful");
        log.info("✓ Verified navigation to actual Sahibinden.com page");
        log.info("========================================");
    }


    @Test
    @DisplayName("Test #2: Click Yepy Button")
    public void testClickYepyButton(){
        log.info("========================================");
        log.info("Starting Test #2: Click Yepy Button");
        log.info("========================================");


    }
    

}
