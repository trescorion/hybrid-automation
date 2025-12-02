package com.sahibinden.uiautomation.tests;

import com.sahibinden.uiautomation.api.AccuWeatherApiClient;
import com.sahibinden.uiautomation.config.WebDriverFactory;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AccuWeather API and UI comparison tests.
 * Validates that API temperature matches UI temperature.
 */
@Slf4j
@SpringBootTest
@Epic("AccuWeather API")
@Feature("Current Conditions")
@DisplayName("AccuWeather API ve UI Karşılaştırma Testleri")
public class AccuWeatherApiTest {
    
    @Autowired
    private WebDriverFactory webDriverFactory;
    
    private final AccuWeatherApiClient apiClient = new AccuWeatherApiClient();
    private WebDriver driver;
    
    private static final String LOCATION = "349727";
    private static final By TEMPERATURE_DISPLAY = By.xpath("//div[@class='display-temp']");
    private static final double TEMPERATURE_TOLERANCE = 1.0;
    
    @BeforeEach
    public void setUp() {
        driver = webDriverFactory.createDriver();
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Story("API ve UI Sıcaklık Karşılaştırması")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("API ve UI Sıcaklık Değerleri Eşleşmeli")
    @Description("AccuWeather API'den alınan sıcaklık değeri ile web sitesinde görüntülenen sıcaklık değerinin eşleşip eşleşmediği kontrol edilir.")
    public void testTemperatureMatchesBetweenApiAndUi() {
        Response response = getApiResponse();
        String responseBody = response.getBody().asString();
        
        ApiWeatherData weatherData = extractWeatherData(response, responseBody);
        
        navigateToWeatherPage(weatherData.link());
        Double uiTemperature = getTemperatureFromUi();
        
        compareTemperatures(weatherData.temperature(), uiTemperature);
    }
    
    @Step("API'den mevcut hava durumu bilgisi alınıyor")
    private Response getApiResponse() {
        Response response = apiClient.getCurrentConditions(LOCATION);
        
        assertThat(response.getStatusCode())
                .as("API response status code should be 200")
                .isEqualTo(200);
        
        assertThat(response.getBody().asString())
                .as("Response body should not be empty")
                .isNotEmpty();
        
        return response;
    }
    
    @Step("JSON response'tan link ve sıcaklık değerleri extract ediliyor")
    private ApiWeatherData extractWeatherData(Response response, String responseBody) {
        Allure.addAttachment("API Response (JSON)", "application/json",
                new ByteArrayInputStream(responseBody.getBytes()), ".json");
        
        JsonPath jsonPath = response.jsonPath();
        String link = jsonPath.getString("[0].Link");
        Double temperature = jsonPath.getDouble("[0].Temperature.Metric.Value");
        
        assertThat(link)
                .as("Link should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        assertThat(temperature)
                .as("API temperature should not be null")
                .isNotNull();
        
        return new ApiWeatherData(link, temperature);
    }
    
    @Step("Web sitesine gidiliyor: {link}")
    private void navigateToWeatherPage(String link) {
        driver.get(link);
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Step("Web sitesinden sıcaklık değeri okunuyor")
    private Double getTemperatureFromUi() {
        try {
            WebElement tempElement = driver.findElement(TEMPERATURE_DISPLAY);
            String tempText = tempElement.getText().trim();
            
            Double temperature = parseTemperature(tempText);
            
            if (temperature == null) {
                tempText = tempElement.getAttribute("textContent");
                if (tempText == null || tempText.trim().isEmpty()) {
                    tempText = tempElement.getAttribute("innerText");
                }
                temperature = parseTemperature(tempText);
            }
            
            assertThat(temperature)
                    .as("UI temperature should not be null")
                    .isNotNull();
            
            return temperature;
            
        } catch (Exception e) {
            log.error("UI'dan sıcaklık değeri okunurken hata oluştu", e);
            throw new RuntimeException("Failed to get temperature from UI", e);
        }
    }
    
    @Step("API ve UI sıcaklık değerleri karşılaştırılıyor")
    private void compareTemperatures(Double apiTemperature, Double uiTemperature) {
        double difference = Math.abs(apiTemperature - uiTemperature);
        
        assertThat(difference)
                .as("Sıcaklık değerleri eşleşmeli. API: %s°C, UI: %s°C, Fark: %s°C, Tolerans: %s°C",
                        apiTemperature, uiTemperature, difference, TEMPERATURE_TOLERANCE)
                .isLessThanOrEqualTo(TEMPERATURE_TOLERANCE);
    }
    
    private Double parseTemperature(String tempText) {
        if (tempText == null || tempText.trim().isEmpty()) {
            return null;
        }
        
        Pattern pattern = Pattern.compile("([-+]?\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(tempText.replace("°", "").replace("C", "").trim());
        
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    private record ApiWeatherData(String link, Double temperature) {}
}
