package com.sahibinden.uiautomation.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base Page Object class implementing common functionality.
 * Follows DRY principle - all pages inherit common methods.
 * Implements Page Object Model pattern.
 */
@Slf4j
public abstract class BasePage {
    
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    
    private static final int DEFAULT_WAIT_TIMEOUT = 10;
    
    /**
     * Constructor initializes PageFactory and WebDriverWait.
     * 
     * @param driver WebDriver instance
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        PageFactory.initElements(driver, this);
        log.debug("Initialized page: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Navigates to the specified URL.
     * 
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
    }
    
    /**
     * Gets the current page URL.
     * 
     * @return current URL
     */

    //test
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    
    /**
     * Gets the current page title.
     * 
     * @return page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Waits for element to be visible.
     * 
     * @param element WebElement to wait for
     * @return the visible WebElement
     */
    protected WebElement waitForVisibility(WebElement element) {
        log.debug("Waiting for element visibility");
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Waits for element to be clickable.
     * 
     * @param element WebElement to wait for
     * @return the clickable WebElement
     */
    protected WebElement waitForClickability(WebElement element) {
        log.debug("Waiting for element clickability");
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Waits for element located by the given locator to be visible.
     * 
     * @param locator By locator
     * @return the visible WebElement
     */
    protected WebElement waitForVisibility(By locator) {
        log.debug("Waiting for element visibility using locator: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Waits for URL to contain the specified fragment.
     * Public method for use in tests and page objects.
     *
     * @param urlFragment URL fragment to wait for
     * @return true if URL contains the fragment within timeout
     */
    public boolean waitForUrlContains(String urlFragment) {
        log.debug("Waiting for URL to contain: {}", urlFragment);
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }
    
    /**
     * Waits for URL to be exactly as specified.
     * 
     * @param url exact URL to wait for
     * @return true if URL matches within timeout
     */
    protected boolean waitForUrlToBe(String url) {
        log.debug("Waiting for URL to be: {}", url);
        return wait.until(ExpectedConditions.urlToBe(url));
    }
    
    /**
     * Clicks on an element with explicit wait.
     * 
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        try {
            waitForClickability(element);
            element.click();
            log.debug("Clicked element: {}", element);
        } catch (Exception e) {
            log.error("Failed to click element: {}", element, e);
            throw e;
        }
    }
    
    /**
     * Sends text to an element with explicit wait.
     * 
     * @param element WebElement to send text to
     * @param text text to send
     */
    protected void sendKeys(WebElement element, String text) {
        try {
            waitForVisibility(element);
            element.clear();
            element.sendKeys(text);
            log.debug("Sent text '{}' to element: {}", text, element);
        } catch (Exception e) {
            log.error("Failed to send text to element: {}", element, e);
            throw e;
        }
    }
    
    /**
     * Gets text from an element with explicit wait.
     * 
     * @param element WebElement to get text from
     * @return element text
     */
    protected String getText(WebElement element) {
        try {
            waitForVisibility(element);
            String text = element.getText();
            log.debug("Got text '{}' from element: {}", text, element);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text from element: {}", element, e);
            throw e;
        }
    }
    
    /**
     * Checks if element is displayed.
     * 
     * @param element WebElement to check
     * @return true if element is displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            log.debug("Element not displayed: {}", element);
            return false;
        }
    }
    
    /**
     * Executes JavaScript on the page.
     * 
     * @param script JavaScript code to execute
     * @param args arguments for the script
     * @return result of script execution
     */
    protected Object executeScript(String script, Object... args) {
        log.debug("Executing JavaScript: {}", script);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, args);
    }
    
    /**
     * Scrolls to element using JavaScript.
     * 
     * @param element WebElement to scroll to
     */
    protected void scrollToElement(WebElement element) {
        log.debug("Scrolling to element: {}", element);
        executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    /**
     * Creates a custom wait with specified timeout.
     *
     * @param timeoutInSeconds timeout in seconds
     * @return WebDriverWait instance
     */
    protected WebDriverWait createWait(int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    /**
     * Generic method to click any element by locator.
     * Follows W3C WebDriver standards - STRICT approach.
     *
     * Does NOT use JavaScript click fallback to ensure real user interaction.
     * If element cannot be clicked, it indicates a genuine UI issue.
     *
     * @param locator By locator (By.id, By.css, By.xpath, etc.)
     * @param elementName descriptive name for logging
     * @throws AssertionError if element cannot be clicked (UI problem detected)
     */
    public void clickElement(By locator, String elementName) {
        log.info("Attempting to click: {}", elementName);
        
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            
            element.click();
            log.info("✓ Successfully clicked: {}", elementName);
            
        } catch (ElementClickInterceptedException e) {
            // Element is covered - this indicates a REAL UI problem!
            log.error("❌ Element '{}' is intercepted by another element", elementName);
            log.error("Intercepting element details: {}", e.getMessage());
            
            // Take screenshot for debugging
            takeScreenshot(elementName + "_click_intercepted");
            
            throw new AssertionError(
                String.format("Cannot click '%s' - element is covered by another element. " +
                             "This indicates a UI issue that needs to be fixed. " +
                             "Check screenshot for details.", elementName),
                e
            );
            
        } catch (TimeoutException e) {
            log.error("❌ Element '{}' not clickable within {} seconds",
                     elementName, DEFAULT_WAIT_TIMEOUT);
            
            takeScreenshot(elementName + "_not_clickable");
            
            throw new AssertionError(
                String.format("Element '%s' not clickable within %d seconds. " +
                             "Check screenshot for details.",
                             elementName, DEFAULT_WAIT_TIMEOUT),
                e
            );
            
        } catch (NoSuchElementException e) {
            log.error("❌ Element '{}' not found on page", elementName);
            
            takeScreenshot(elementName + "_not_found");
            
            throw new AssertionError(
                String.format("Element '%s' not found on page. Check screenshot.", elementName),
                e
            );
        }
    }
    
    /**
     * Takes screenshot for debugging purposes.
     * Screenshots are saved in build/screenshots directory.
     *
     * @param filename base filename for screenshot
     */
    protected void takeScreenshot(String filename) {
        try {
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
            
            // Create screenshots directory
            File screenshotsDir = new File("build/screenshots");
            screenshotsDir.mkdirs();
            
            // Generate unique filename with timestamp
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            );
            String fileName = String.format("%s_%s.png", filename, timestamp);
            File destination = new File(screenshotsDir, fileName);
            
            // Copy screenshot
            Files.copy(screenshot.toPath(), destination.toPath(),
                      StandardCopyOption.REPLACE_EXISTING);
            
            log.info("📸 Screenshot saved: {}", destination.getAbsolutePath());
            
        } catch (Exception e) {
            log.warn("Failed to take screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Generic method to check if element is displayed.
     *
     * @param locator By locator
     * @param elementName descriptive name for logging
     * @return true if element is visible
     */
    public boolean isElementDisplayed(By locator, String elementName) {
        try {
            WebElement element = driver.findElement(locator);
            boolean displayed = element.isDisplayed();
            log.debug("{} displayed: {}", elementName, displayed);
            return displayed;
        } catch (Exception e) {
            log.debug("{} not found: {}", elementName, e.getMessage());
            return false;
        }
    }
}