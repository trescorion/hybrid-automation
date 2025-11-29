package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import com.sahibinden.uiautomation.pages.YepyPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Yepy Category Tests")
public class YepyTest extends BaseTest {

    @Test
    @DisplayName("Verify price order ascending functionality")
    public void testPriceOrderAscending() {
        navigateToHomePageAndVerify();
        // Step 1: Navigate to Yepy category
        verifyYepyLinkIsDisplayed();
        clickYepyLink();
        verifyUrlContainsYepy();
        // Step 2: Navigate to Yenilenmiş Telefonlar
        verifyCihazAraLinkIsDisplayed();
        clickCihazAraButton();
        verifyUrlContainsYenilenmisTelefonlar();
        // Step 3: Open Gelişmiş Sıralama
        verifyGelismisSiralamaLinkIsDisplayed();
        clickGelismisSiralamaDropdown();
        // Step 4: Select Price Ascending
        verifyFiyatDusuktenYuksegeLinkIsDisplayed();
        clickFiyatDusuktenYuksegeLink();
        verifyUrlContainsPriceAsc();
        // Step 5: Verify prices are sorted in ascending order
        verifyPricesAreSortedAscending();
    }

    @Test
    @DisplayName("Verify price order ascending functionality")
    public void testPriceOrderDescending() {
        // Setup: Navigate to home page
        navigateToHomePageAndVerify();
        // Step 1: Navigate to Yepy category
        verifyYepyLinkIsDisplayed();
        clickYepyLink();
        verifyUrlContainsYepy();
        // Step 2: Navigate to Yenilenmiş Telefonlar
        verifyCihazAraLinkIsDisplayed();
        clickCihazAraButton();
        verifyUrlContainsYenilenmisTelefonlar();
        // Step 3: Open Gelişmiş Sıralama
        verifyGelismisSiralamaLinkIsDisplayed();
        clickGelismisSiralamaDropdown();
        // Step 4: Select Price Ascending
        verifyFiyatDusuktenYuksegeLinkIsDisplayed();
        clickFiyatYuksektenDusuge();
        verifyUrlContainsPriceDesc();
        // Step 5: Verify prices are sorted in ascending order
        verifyPricesAreSortedDescending();
    }

    @Test
    @DisplayName("Verify maximum price filter with descending sort")
    public void testMaxPriceFilterWithDescendingSort() {
        // Setup: Navigate to Yenilenmiş Telefonlar page
        navigateToHomePageAndVerify();
        verifyYepyLinkIsDisplayed();
        clickYepyLink();
        verifyUrlContainsYepy();
        verifyCihazAraLinkIsDisplayed();
        clickCihazAraButton();
        verifyUrlContainsYenilenmisTelefonlar();

        // Step 1: Set maximum price filter to 9000
        verifyEnYuksekFiyatInputIsDisplayed();
        setMaxPrice(9000);

        // Step 2: Click search button to apply filter
        clickSearchButton();

        // Step 3: Wait for URL to contain price_max parameter
        waitForPriceMaxInUrl(9000);

        // Step 4: Sort by price descending
        verifyGelismisSiralamaLinkIsDisplayed();
        clickGelismisSiralamaDropdown();
        verifyFiyatYuksektenDusugeLinkIsDisplayed();
        clickFiyatYuksektenDusuge();
        verifyUrlContainsPriceDesc();
        verifyFirstPriceIsWithinMaxLimit(9000);

        // Step 5: Verify highest price is not greater than 9000
        verifyFirstPriceIsWithinMaxLimit(9000);

    }

    @Test
    @DisplayName("Verify minimum price filter with ascending sort")
    public void testMinPriceFilterWithAscendingSort() {
        // Setup: Navigate to Yenilenmiş Telefonlar page
        navigateToHomePageAndVerify();
        verifyYepyLinkIsDisplayed();
        clickYepyLink();
        verifyUrlContainsYepy();
        verifyCihazAraLinkIsDisplayed();
        clickCihazAraButton();
        verifyUrlContainsYenilenmisTelefonlar();

        // Step 1: Set minimum price filter to 5000
        verifyEnDusukFiyatInputIsDisplayed();
        setMinPrice(5000);

        // Step 2: Click search button to apply filter
        clickSearchButton();

        // Step 3: Wait for URL to contain price_min parameter
        waitForPriceMinInUrl(5000);

        // Step 4: Sort by price ascending
        verifyGelismisSiralamaLinkIsDisplayed();
        clickGelismisSiralamaDropdown();
        verifyFiyatDusuktenYuksegeLinkIsDisplayed();
        clickFiyatDusuktenYuksegeLink();
        verifyUrlContainsPriceAsc();

        // Step 5: Verify lowest price is not less than 5000
        verifyFirstPriceIsWithinMinLimit(5000);


    }

    // ========== Yepy Category Navigation Steps ==========
    private void verifyYepyLinkIsDisplayed() {
        assertThat(homePage.isElementDisplayed(SahibindenHomePage.YEPY_BUTTON, "Yepy"))
                .as("Yepy link should be visible on home page")
                .isTrue();
    }

    private void clickYepyLink() {
        homePage.clickElement(SahibindenHomePage.YEPY_BUTTON, "Yepy");
    }

    private void verifyUrlContainsYepy() {
        homePage.waitForUrlContains("/yepy");
        assertThat(getCurrentUrl())
                .as("URL should contain '/yepy' after clicking Yepy link")
                .contains("/yepy");
    }

    // ========== Yenilenmiş Telefonlar Navigation Steps ==========
    private void verifyCihazAraLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.CIHAZ_ARA_BUTTON, "Cihaz Ara Link"))
                .as("Cihaz ara link should be visible on Yepy page")
                .isTrue();
    }

    private void clickCihazAraButton() {
        yepyPage.clickElement(YepyPage.CIHAZ_ARA_BUTTON, "Cihaz Ara Link");
    }

    private void verifyUrlContainsYenilenmisTelefonlar() {
        yepyPage.waitForUrlContains("/yepy/yenilenmis-telefonlar");
        assertThat(getCurrentUrl())
                .as("URL should contain '/yepy/yenilenmis-telefonlar' after clicking Cihaz Ara link")
                .contains("/yepy/yenilenmis-telefonlar");
    }

    // ========== Gelişmiş Sıralama Steps ==========
    private void verifyGelismisSiralamaLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.GELISMIS_SIRALAMA_DROPDOWN, "Gelişmiş Sıralama Link"))
                .as("Gelişmiş sıralama link should be visible")
                .isTrue();
    }

    private void clickGelismisSiralamaDropdown() {
        yepyPage.clickElement(YepyPage.GELISMIS_SIRALAMA_DROPDOWN, "Gelişmiş Sıralama");
    }

    // ========== Price Sorting Steps ==========

    private void clickAraFilterButton() {
        yepyPage.clickElement(YepyPage.ARA_BUTTON, "Cihaz Ara Link");
    }
    private void verifyFiyatDusuktenYuksegeLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.FIYAT_DUSUKTEN_YUKSEGE_BUTTON, "Fiyat: Düşükten yükseğe Link"))
                .as("Fiyat: Düşükten yükseğe link should be visible")
                .isTrue();
    }

    private void verifyFiyatYuksektenDusugeLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.FIYAT_YUKSEKTEN_DUSUGE_BUTTON, "Fiyat: Yüksekten düşüğe Link"))
                .as("Fiyat: Yüksekten düşüğe link should be visible")
                .isTrue();
    }

    private void clickFiyatDusuktenYuksegeLink() {
        yepyPage.clickElement(YepyPage.FIYAT_DUSUKTEN_YUKSEGE_BUTTON, "Fiyat: Düşükten yükseğe");
    }


    private void clickFiyatYuksektenDusuge() {
        yepyPage.clickElement(YepyPage.FIYAT_YUKSEKTEN_DUSUGE_BUTTON, "Fiyat: Yüksekten düşüğe");
    }

    private void verifyUrlContainsPriceAsc() {
        yepyPage.waitForUrlContains("sorting=price_asc");
        assertThat(getCurrentUrl())
                .as("URL should contain 'sorting=price_asc' after clicking price ascending link")
                .contains("sorting=price_asc");
    }


    private void verifyUrlContainsPriceDesc() {
        yepyPage.waitForUrlContains("sorting=price_desc");
        assertThat(getCurrentUrl())
                .as("URL should contain 'sorting=price_desc' after clicking price ascending link")
                .contains("sorting=price_desc");
    }


    private void verifyPricesAreSortedAscending() {
        yepyPage.waitForUrlContains("sorting=price_asc");
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty")
                .isNotEmpty();

        // Generic metod kullanarak
        assertThat(yepyPage.arePricesSorted(true))
                .as("Prices should be sorted in ascending order")
                .isTrue();

        // Veya convenience metod
        // assertThat(yepyPage.arePricesSortedAscending()).isTrue();

        log.info("✓ Verified {} prices are sorted in ascending order", prices.size());
    }

    private void verifyPricesAreSortedDescending() {
        yepyPage.waitForUrlContains("sorting=price_desc");
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty")
                .isNotEmpty();

        // Generic metod kullanarak
        assertThat(yepyPage.arePricesSorted(false))
                .as("Prices should be sorted in descending order")
                .isTrue();

        // Veya convenience metod
        // assertThat(yepyPage.arePricesSortedDescending()).isTrue();

        log.info("✓ Verified {} prices are sorted in descending order", prices.size());
    }

    // ========== Price Filter Steps ==========

    private void verifyEnYuksekFiyatInputIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.EN_YUKSEK_FIYAT_INPUT, "En Yüksek Fiyat Input"))
                .as("En yüksek fiyat input should be visible")
                .isTrue();
    }

    private void verifyEnDusukFiyatInputIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.EN_DUSUK_FIYAT_INPUT, "En Düşük Fiyat Input"))
                .as("En düşük fiyat input should be visible")
                .isTrue();
    }

    private void setMaxPrice(int maxPrice) {
        yepyPage.setMaxPrice(maxPrice);
    }

    private void setMinPrice(int minPrice) {
        yepyPage.setMinPrice(minPrice);
    }

    private void clickSearchButton() {
        yepyPage.clickSearchButton();
    }

    private void waitForPriceMaxInUrl(int maxPrice) {
        yepyPage.waitForPriceMaxInUrl(maxPrice);
    }

    private void waitForPriceMinInUrl(int minPrice) {
        yepyPage.waitForPriceMinInUrl(minPrice);
    }

    private void verifyFirstPriceIsWithinMaxLimit(double maxPrice) {
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty after filtering and sorting")
                .isNotEmpty();

        double firstPrice = yepyPage.getFirstPrice();

        assertThat(firstPrice)
                .as("First price %s (after descending sort) should be <= maximum price filter %s",
                        firstPrice, maxPrice)
                .isLessThanOrEqualTo(maxPrice);

        log.info("✓ Verified first price {} is within max limit {}", firstPrice, maxPrice);
    }

    private void verifyFirstPriceIsWithinMinLimit(double minPrice) {
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty after filtering and sorting")
                .isNotEmpty();

        double firstPrice = yepyPage.getFirstPrice();

        assertThat(firstPrice)
                .as("First price %s (after ascending sort) should be >= minimum price filter %s",
                        firstPrice, minPrice)
                .isGreaterThanOrEqualTo(minPrice);

        log.info("✓ Verified first price {} is within min limit {}", firstPrice, minPrice);
    }

}
