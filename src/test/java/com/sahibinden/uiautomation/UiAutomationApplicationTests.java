package com.sahibinden.uiautomation;

import com.sahibinden.uiautomation.config.TestConfig;
import com.sahibinden.uiautomation.config.WebDriverFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UiAutomationApplicationTests {

    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    private TestConfig testConfig;

    @Test
    void contextLoads() {
        // Spring context yüklendi mi?
    }

    @Test
    void dependencyInjectionWorks() {
        // Spring beans inject edilebildi mi?
        assertThat(webDriverFactory).isNotNull();
        assertThat(testConfig).isNotNull();
    }

    @Test
    void configurationIsValid() {
        // Configuration değerleri doğru mu?
        assertThat(testConfig.getBaseUrl()).isNotEmpty();
    }
}