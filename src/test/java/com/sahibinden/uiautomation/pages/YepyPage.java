package com.sahibinden.uiautomation.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class YepyPage extends BasePage{

    public static final By CIHAZ_ARA_BUTTON = By.xpath("//a[normalize-space(text())='Cihaz ara']");
    public static final By GELISMIS_SIRALAMA_DROPDOWN = By.id("advancedSorting");
    public static final By FIYAT_DUSUKTEN_YUKSEGE_BUTTON = By.xpath("//a[@title='Fiyat: Düşükten yükseğe']");
    public static final By FIYAT_YUKSEKTEN_DUSUGE_BUTTON = By.xpath("//a[@title='Fiyat: Yüksekten düşüğe']");
    public static final By ALL_PRICE_ELEMENTS = By.xpath(
            "//div[contains(@class, 'searchResultsPriceValue')]//span[contains(@class, 'classified-price-container') or text()]"
    );
    public static final By EN_YUKSEK_FIYAT_INPUT = By.cssSelector("input[name='price_max']");

    /**
     * Constructor initializes PageFactory and WebDriverWait.
     *
     * @param driver WebDriver instance
     */
    public YepyPage(WebDriver driver) {
        super(driver);
        log.info("Initialized YepyPage");
    }


    public List<WebElement> getAllPriceElements() {
        log.info("Finding all price elements on the page...");
        List<WebElement> priceElements = driver.findElements(ALL_PRICE_ELEMENTS);
        log.info("Found {} price elements", priceElements.size());
        return priceElements;
    }

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



}
