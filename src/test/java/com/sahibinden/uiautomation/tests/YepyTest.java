package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Yepy category navigation.
 * 
 * Logging Strategy:
 * - Test methods contain minimal logging (only test-level summary)
 * - Detailed operation logging is done in Page Objects and BaseTest
 * - This follows DRY principle and keeps tests clean
 * 
 * Best Practices:
 * - KISS: Simple, straightforward test logic
 * - DRY: Logging done in methods, not duplicated in tests
 * - SOLID: Single Responsibility
 * - Page Object Model: Uses SahibindenHomePage via BaseTest
 */
@Slf4j
@DisplayName("Yepy Category Tests")
public class YepyTest extends BaseTest {
    
    @Test
    @DisplayName("Navigate to Yepy category and verify link interaction")
    public void testYepyNavigation() {
        navigateToSahibinden();
        assertThat(isOnSahibinden())
            .as("Should be on Sahibinden.com")
            .isTrue();
        assertThat(homePage.isCategoryLinkDisplayed(SahibindenHomePage.YEPY_LINK, "Yepy"))
            .as("Yepy link should be visible")
            .isTrue();
        homePage.clickCategoryLink(SahibindenHomePage.YEPY_LINK, "Yepy");
        homePage.waitForUrlContains("/yepy");
        assertThat(getCurrentUrl())
            .as("URL should contain '/yepy'")
            .contains("/yepy");
        // BaseTest @AfterEach logs completion with final URL
    }

}
