package com.sahibinden.uiautomation.pages;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class YepyPage extends BasePage {

    public static final By CIHAZ_ARA_BUTTON = By.xpath("//a[normalize-space(text())='Cihaz ara']");
    public static final By GELISMIS_SIRALAMA_DROPDOWN = By.id("advancedSorting");
    public static final By FIYAT_DUSUKTEN_YUKSEGE_BUTTON = By.xpath("//a[@title='Fiyat: Düşükten yükseğe']");
    public static final By FIYAT_YUKSEKTEN_DUSUGE_BUTTON = By.xpath("//a[@title='Fiyat: Yüksekten düşüğe']");
    public static final By ALL_PRICE_ELEMENTS = By.xpath(
            "//div[contains(@class, 'searchResultsPriceValue')]//span[contains(@class, 'classified-price-container') or text()]"
    );
    public static final By EN_YUKSEK_FIYAT_INPUT = By.cssSelector("input[name='price_max']");
    public static final By EN_DUSUK_FIYAT_INPUT = By.cssSelector("input[name='price_min']");
    public static final By ARA_BUTTON = By.xpath("//button[normalize-space(text())='Ara']");

    public static final By KOZMETIK_DURUM_IYI_CHECKBOX = By.xpath("//div[@class='form-check'][.//label[normalize-space(text())='İyi']]//input[@type='checkbox']");
    public static final By DEPOLAMA_256GB_CHECKBOX = By.xpath(
            "//div[@class='form-check'][.//label[normalize-space(text())='128 GB']]//input[@type='checkbox']"
    );
    public static final By RENK_ALTIN_CHECKBOX = By.xpath("//div[@class='form-check'][.//label[normalize-space(text())='Altın']]//input[@type='checkbox']");

    // First product link - excludes banner items, only targets real product links with detail page URLs
    public static final By FIRST_PRODUCT_LINK = By.xpath(
            "//div[contains(@class, 'refurbishment-content')]/ul/li[1]//a[contains(@class, 'refurbishment-classified-url')]"
    );
    public static final By IYI_DURUMDA = By.xpath("//h3[@data-access='detail' and normalize-space(text())='İyi durumda']");
    public static final By ALTIN_RENGI = By.xpath("//span[@data-access='selected-color' and normalize-space(text())='Altın']");
    /**
     * Constructor initializes PageFactory and WebDriverWait.
     *
     * @param driver WebDriver instance
     */
    public YepyPage(WebDriver driver) {
        super(driver);
        log.info("Initialized YepyPage");
    }


    @Step("Sayfadaki tüm fiyat elementleri bulunur")
    public List<WebElement> getAllPriceElements() {
        log.info("Finding all price elements on the page...");
        List<WebElement> priceElements = driver.findElements(ALL_PRICE_ELEMENTS);
        log.info("Found {} price elements", priceElements.size());
        return priceElements;
    }

    @Step("Listelenen ilk fiyat alınır")
    public double getFirstPrice() {
        List<Double> prices = getAllPricesAsDoubles();

        if (prices.isEmpty()) {
            log.warn("No prices found on the page");
            return 0.0;
        }

        double firstPrice = prices.get(0);
        log.info("First price in list: {}", firstPrice);
        return firstPrice;
    }

    @Step("İlk fiyatın limit dahilinde olup olmadığı kontrol edilir: {limit} ({isMax})")
    public boolean isFirstPriceWithinLimit(boolean isMax, double limit) {
        double firstPrice = getFirstPrice();
        String limitType = isMax ? "maximum" : "minimum";

        // For descending sort (isMax=true): first price should be <= maxPrice
        // For ascending sort (isMax=false): first price should be >= minPrice
        boolean isValid = isMax
                ? firstPrice <= limit
                : firstPrice >= limit;

        if (isValid) {
            log.info("✓ First price {} is within {} limit {}", firstPrice, limitType, limit);
        } else {
            log.error("❌ First price {} exceeds {} limit {}", firstPrice, limitType, limit);
        }

        return isValid;
    }


    @Step("Tüm fiyatlar liste olarak çekilir ve double'a çevrilir")
    public List<Double> getAllPricesAsDoubles() {
        List<WebElement> priceElements = getAllPriceElements();
        List<Double> prices = new ArrayList<>();

        for (WebElement element : priceElements) {
            try {
                String priceText = element.getText().trim();
                if (priceText.isEmpty()) {
                    continue; // Skip empty elements
                }

                // Parse price: "4.999 TL" -> 4999.0
                double price = parsePrice(priceText);
                prices.add(price);

            } catch (Exception e) {
                log.warn("Failed to parse price from element: {}", element.getText(), e);
            }
        }

        log.info("Extracted {} prices: {}", prices.size(), prices);
        return prices;
    }

    private double parsePrice(String priceText) {
        // Remove "TL" and whitespace
        String cleaned = priceText.replaceAll("TL", "").trim();

        // Remove thousand separators (dots) and replace comma with dot for decimal
        // "4.999" -> "4999"
        // "1.234,56" -> "1234.56"
        cleaned = cleaned.replace(".", ""); // Remove thousand separators
        cleaned = cleaned.replace(",", "."); // Replace comma with dot for decimal

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            log.error("Failed to parse price: {}", priceText);
            throw new IllegalArgumentException("Invalid price format: " + priceText, e);
        }
    }


    @Step("Fiyatların {ascending} sıraya göre olup olmadığı kontrol edilir")
    public boolean arePricesSorted(boolean ascending) {
        List<Double> prices = getAllPricesAsDoubles();

        if (prices.isEmpty()) {
            log.warn("No prices found on the page");
            return false;
        }

        if (prices.size() == 1) {
            log.info("Only one price found, considered sorted");
            return true;
        }

        String orderName = ascending ? "ascending" : "descending";

        for (int i = 0; i < prices.size() - 1; i++) {
            double current = prices.get(i);
            double next = prices.get(i + 1);

            boolean isValid = ascending ? current <= next : current >= next;

            if (!isValid) {
                log.error("Prices not sorted {}. Found {} {} {} at positions {} and {}",
                        orderName, current, ascending ? ">" : "<", next, i, i + 1);
                return false;
            }
        }

        log.info("✓ All {} prices are sorted in {} order", prices.size(), orderName);
        return true;
    }


    @Step("Maksimum fiyat girilir: {maxPrice}")
    public void setMaxPrice(int maxPrice) {
        log.info("Setting maximum price filter to: {}", maxPrice);
        WebElement priceInput = waitForVisibility(EN_YUKSEK_FIYAT_INPUT);
        priceInput.clear();
        priceInput.sendKeys(String.valueOf(maxPrice));
        log.info("✓ Maximum price filter set to: {}", maxPrice);
    }

    @Step("Minimum fiyat girilir: {minPrice}")
    public int setMinPrice(int minPrice) {
        log.info("Setting minimum price filter to: {}", minPrice);
        WebElement priceInput = waitForVisibility(EN_DUSUK_FIYAT_INPUT);
        priceInput.clear();
        priceInput.sendKeys(String.valueOf(minPrice));
        log.info("✓ Minimum price filter set to: {}", minPrice);
        return minPrice;
    }

    @Step("Arama butonuna tıklanır")
    public void clickSearchButton() {
        log.info("Clicking search button to apply filters...");
        clickElement(ARA_BUTTON, "Ara Button");
        log.info("✓ Search button clicked");
    }

    @Step("URL'in maksimum fiyat filtresini içermesi beklenir: {maxPrice}")
    public void waitForPriceMaxInUrl(int maxPrice) {
        String urlFragment = "price_max=" + maxPrice;
        log.info("Waiting for URL to contain: {}", urlFragment);
        waitForUrlContains(urlFragment);
        log.info("✓ Price max filter applied - URL contains {}", urlFragment);
    }

    @Step("URL'in minimum fiyat filtresini içermesi beklenir: {minPrice}")
    public void waitForPriceMinInUrl(int minPrice) {
        String urlFragment = "price_min=" + minPrice;
        log.info("Waiting for URL to contain: {}", urlFragment);
        waitForUrlContains(urlFragment);
        log.info("✓ Price min filter applied - URL contains {}", urlFragment);
    }

    // ========== Business Logic Methods (Page Object Pattern) ==========

    /**
     * Applies price sorting (ascending or descending) on the page.
     * This is a page-level operation, so it belongs in the Page Object.
     *
     * @param ascending true for ascending, false for descending
     */
    @Step("Kullanıcı '{ascending}' parametresi ile fiyat sıralamasını uygular")
    public void applyPriceSorting(boolean ascending) {
        log.info("Applying price sorting: {}", ascending ? "ascending" : "descending");

        clickElement(GELISMIS_SIRALAMA_DROPDOWN, "Gelişmiş Sıralama");

        if (ascending) {
            clickElement(FIYAT_DUSUKTEN_YUKSEGE_BUTTON, "Fiyat: Düşükten yükseğe");
            waitForUrlContains("sorting=price_asc");
        } else {
            clickElement(FIYAT_YUKSEKTEN_DUSUGE_BUTTON, "Fiyat: Yüksekten düşüğe");
            waitForUrlContains("sorting=price_desc");
        }

        log.info("✓ Price sorting applied: {}", ascending ? "ascending" : "descending");
    }

    /**
     * Applies a price filter (min or max) and waits for URL update.
     * This is a page-level operation, so it belongs in the Page Object.
     *
     * @param price The price value to filter by
     * @param isMax true for maximum price, false for minimum price
     */
    @Step("Maksimum fiyat filtresi uygulanır: {price} TL")
    public void applyPriceFilter(int price, boolean isMax) {
        log.info("Applying {} price filter: {}", isMax ? "maximum" : "minimum", price);

        if (isMax) {
            setMaxPrice(price);
        } else {
            setMinPrice(price);
        }

        clickSearchButton();

        if (isMax) {
            waitForPriceMaxInUrl(price);
        } else {
            waitForPriceMinInUrl(price);
        }

        log.info("✓ Price filter applied: {} = {}", isMax ? "max" : "min", price);
    }

    /**
     * Clicks a checkbox by its locator.
     * Generic method for any checkbox on the page.
     *
     * @param checkboxLocator The By locator for the checkbox
     * @param checkboxName    Descriptive name for logging
     */
    @Step("Checkbox işaretleniyor: {checkboxName}")
    public void clickCheckbox(By checkboxLocator, String checkboxName) {
        log.info("Clicking checkbox: {}", checkboxName);
        clickElement(checkboxLocator, checkboxName);
        log.info("✓ Checkbox clicked: {}", checkboxName);
    }
}
