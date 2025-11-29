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
    private void verifyFiyatDusuktenYuksegeLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.FIYAT_DUSUKTEN_YUKSEGE_BUTTON, "Fiyat: Düşükten yükseğe Link"))
                .as("Fiyat: Düşükten yükseğe link should be visible")
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


}
