package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.pages.SahibindenHomePage;
import com.sahibinden.uiautomation.pages.YepyPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("Sahibinden UI Otomasyon")
@Feature("Yepy - Yenilenmiş Telefonlar")
@DisplayName("Yepy Kategori Testleri")
public class YepyTest extends BaseTest {

    @Test
    @Story("Fiyat Sıralama")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Artan Fiyat Sıralaması Kontrolü")
    @Description("Kullanıcı 'Fiyat: Düşükten Yükseğe' seçeneğini seçtiğinde ürünler ucuzdan pahalıya sıralanmalıdır.")
    public void testPriceOrderAscending() {
        navigateToYenilenmisTelefonlar();
        yepyPage.applyPriceSorting(true);
        verifyPricesAreSorted(true);
    }

    @Test
    @Story("Fiyat Sıralama")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Azalan Fiyat Sıralaması Kontrolü")
    @Description("Kullanıcı 'Fiyat: Yüksekten Düşüğe' seçeneğini seçtiğinde ürünler pahalıdan ucuza sıralanmalıdır.")
    public void testPriceOrderDescending() {
        navigateToYenilenmisTelefonlar();
        yepyPage.applyPriceSorting(false);
        verifyPricesAreSorted(true);
    }

    @Test
    @Story("Fiyat Filtreleme")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Maksimum Fiyat ve Sıralama Birlikte Kullanımı")
    @Description("Kullanıcı maksimum fiyat belirleyip sıralama yaptığında, hem fiyat limiti hem de sıralama kuralı geçerli olmalıdır.")
    public void testMaxPriceFilterWithDescendingSort() {
        navigateToYenilenmisTelefonlar();

        // Apply maximum price filter
        yepyPage.applyPriceFilter(9000, true);

        // Sort by price descending and verify
        yepyPage.applyPriceSorting(false);
        verifyFirstPriceIsWithinLimit(9000, true);
    }

    @Test
    @DisplayName("Verify minimum price filter with ascending sort")
    public void testMinPriceFilterWithAscendingSort() {
        navigateToYenilenmisTelefonlar();

        // Apply minimum price filter
        yepyPage.applyPriceFilter(5000, false);

        // Sort by price ascending and verify
        yepyPage.applyPriceSorting(true);
        verifyFirstPriceIsWithinLimit(5000, false);
    }

    @Test
    @DisplayName("Multiple filters should be applied")
    public void testMultipleFiltersShouldBeApplied() {
        navigateToYenilenmisTelefonlar();
        yepyPage.clickCheckbox(YepyPage.KOZMETIK_DURUM_IYI_CHECKBOX, "İyi Durum");
        yepyPage.clickCheckbox(YepyPage.RENK_ALTIN_CHECKBOX, "Altın Renk");
        yepyPage.clickSearchButton();

        verifyUrlContains("/apple-cep-telefonu?");
        yepyPage.clickElement(YepyPage.FIRST_PRODUCT_LINK, "First Product");
        verifyUrlContains("/yepy/yenilenmis-telefonlar/detay/");
        yepyPage.isElementDisplayed(YepyPage.IYI_DURUMDA, "İyi durumda");
        yepyPage.isElementDisplayed(YepyPage.ALTIN_RENGI, "Altın rengi");
    }

    // ========== Navigation Helper Methods ==========

    private void navigateToYepy() {
        navigateToHomePageAndVerify();
        verifyYepyLinkIsDisplayed();
        clickYepyLink();
        verifyUrlContains("/yepy");
    }

    private void navigateToYenilenmisTelefonlar() {
        navigateToYepy();
        verifyCihazAraLinkIsDisplayed();
        clickCihazAraButton();
        verifyUrlContains("/yepy/yenilenmis-telefonlar");
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

    // ========== Yenilenmiş Telefonlar Navigation Steps ==========

    private void verifyCihazAraLinkIsDisplayed() {
        assertThat(yepyPage.isElementDisplayed(YepyPage.CIHAZ_ARA_BUTTON, "Cihaz Ara Link"))
                .as("Cihaz ara link should be visible on Yepy page")
                .isTrue();
    }

    private void clickCihazAraButton() {
        yepyPage.clickElement(YepyPage.CIHAZ_ARA_BUTTON, "Cihaz Ara Link");
    }

    // ========== URL Verification (Parametrized) ==========

    private void verifyUrlContains(String urlFragment) {
        yepyPage.waitForUrlContains(urlFragment);
        assertThat(getCurrentUrl())
                .as("URL should contain '%s'", urlFragment)
                .contains(urlFragment);
    }

    // ========== Price Sorting Verification (Test Assertions) ==========

    private void verifyPricesAreSorted(boolean ascending) {
        String sortType = ascending ? "price_asc" : "price_desc";
        yepyPage.waitForUrlContains("sorting=" + sortType);
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty")
                .isNotEmpty();

        assertThat(yepyPage.arePricesSorted(ascending))
                .as("Prices should be sorted in %s order", ascending ? "ascending" : "descending")
                .isTrue();

        log.info("✓ Verified {} prices are sorted in {} order", prices.size(),
                ascending ? "ascending" : "descending");
    }

    // ========== Price Filter Verification (Test Assertions) ==========

    private void verifyFirstPriceIsWithinLimit(double limit, boolean isMax) {
        List<Double> prices = yepyPage.getAllPricesAsDoubles();

        assertThat(prices)
                .as("Price list should not be empty after filtering and sorting")
                .isNotEmpty();

        double firstPrice = yepyPage.getFirstPrice();
        String limitType = isMax ? "maximum" : "minimum";
        String comparison = isMax ? "<=" : ">=";

        if (isMax) {
            assertThat(firstPrice)
                    .as("First price %s (after descending sort) should be %s %s price filter %s",
                            firstPrice, comparison, limitType, limit)
                    .isLessThanOrEqualTo(limit);
        } else {
            assertThat(firstPrice)
                    .as("First price %s (after ascending sort) should be %s %s price filter %s",
                            firstPrice, comparison, limitType, limit)
                    .isGreaterThanOrEqualTo(limit);
        }

        log.info("✓ Verified first price {} is within {} limit {}", firstPrice, limitType, limit);
    }


}
