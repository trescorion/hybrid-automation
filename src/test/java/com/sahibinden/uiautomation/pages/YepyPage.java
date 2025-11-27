package com.sahibinden.uiautomation.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class YepyPage extends BasePage{

    public static final By CIHAZ_ARA_LINK = By.xpath("//a[normalize-space(text())='Cihaz ara']");
    /**
     * Constructor initializes PageFactory and WebDriverWait.
     *
     * @param driver WebDriver instance
     */
    public YepyPage(WebDriver driver) {
        super(driver);
        log.info("Initialized YepyPage");
    }


}
