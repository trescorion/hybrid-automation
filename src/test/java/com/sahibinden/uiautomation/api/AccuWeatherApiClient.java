package com.sahibinden.uiautomation.api;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;

/**
 * AccuWeather API client using RestAssured.
 * Handles API calls with Bearer Token authentication.
 */
@Slf4j
public class AccuWeatherApiClient {
    
    private static final String BASE_URL = "https://dataservice.accuweather.com";
    private static final String API_KEY = "zpka_6052ac2819484b93b9eb200dda2f8c74_32b7d4d9";
    
    static {
        RestAssured.baseURI = BASE_URL;
    }
    
    /**
     * Gets current conditions for a location.
     * 
     * @param locationKey location key (e.g., 349727)
     * @return API response
     */
    public Response getCurrentConditions(String locationKey) {
        log.info("Getting current conditions for location key: {}", locationKey);
        
        Response response = given()
                .header(new Header("Authorization", "Bearer " + API_KEY))
                .pathParam("locationKey", locationKey)
                .when()
                .get("/currentconditions/v1/{locationKey}")
                .then()
                .extract()
                .response();
        
        log.info("Response status code: {}", response.getStatusCode());
        log.info("Response body: {}", response.getBody().asString());
        
        return response;
    }
    
    /**
     * Gets current conditions for the default location (349727).
     * 
     * @return API response
     */
    public Response getCurrentConditions() {
        return getCurrentConditions("349727");
    }
}
