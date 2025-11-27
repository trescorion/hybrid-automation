package com.sahibinden.uiautomation.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
     * 
     * @param urlFragment URL fragment to wait for
     * @return true if URL contains the fragment within timeout
     */
    protected boolean waitForUrlContains(String urlFragment) {
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
}