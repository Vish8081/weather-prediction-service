package com.example.weather.service;

import com.example.weather.model.DailyForecast;
import com.example.weather.model.WeatherForecastResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
class WeatherServiceIntegrationTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = restTemplateBuilder.build();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        // Inject the mocked RestTemplate into the service using reflection
        ReflectionTestUtils.setField(weatherService, "restTemplate", restTemplate);
    }

    @Test
    void getWeatherForecast_OnlineMode_ReturnsValidResponse() throws Exception {
        // Mock API response
        String mockResponse = """
            {
                "list": [
                    {
                        "dt": %d,
                        "main": {"temp": 22.5, "temp_max": 25.0, "temp_min": 20.0},
                        "wind": {"speed": 5.2},
                        "weather": [{"main": "Clear"}]
                    },
                    {
                        "dt": %d,
                        "main": {"temp": 24.1, "temp_max": 26.0, "temp_min": 22.0},
                        "wind": {"speed": 6.0},
                        "weather": [{"main": "Clouds"}]
                    }
                ]
            }
            """.formatted(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
        );

        mockServer.expect(requestTo(containsString("api.openweathermap.org")))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        WeatherForecastResponse response = weatherService.getWeatherForecast("London", false);

        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void getWeatherForecast_OfflineMode_ReturnsMockData() {
        WeatherForecastResponse response = weatherService.getWeatherForecast("Paris", true);

        assertNotNull(response);
        List<DailyForecast> forecasts = response.getForecasts();
        assertEquals(3, forecasts.size());
        assertEquals(LocalDate.now(), forecasts.get(0).getDate());
        assertEquals(LocalDate.now().plusDays(1), forecasts.get(1).getDate());
        assertEquals(LocalDate.now().plusDays(2), forecasts.get(2).getDate());
    }

    @Test
    void getWeatherForecast_ApiFailure_FallsBackToOffline() throws Exception {
        mockServer.expect(requestTo(containsString("api.openweathermap.org")))
                .andRespond(withServerError());

        WeatherForecastResponse response = weatherService.getWeatherForecast("Berlin", false);

        assertNotNull(response);
        assertEquals(3, response.getForecasts().size());
        mockServer.verify();
    }

    @Test
    void getOfflineForecast_ContainsRecommendations() {
        WeatherForecastResponse response = weatherService.getWeatherForecast("Madrid", true);

        assertNotNull(response);
        List<DailyForecast> forecasts = response.getForecasts();

        // Debug print to see what recommendations were generated
        forecasts.forEach(f -> System.out.println(
                "Date: " + f.getDate() +
                        ", Condition: " + f.getWeatherCondition() +
                        ", Recommendations: " + f.getRecommendations()
        ));

        // Check that at least one forecast has recommendations
        assertFalse(forecasts.stream()
                        .anyMatch(f -> !f.getRecommendations().isEmpty()),
                "At least one daily forecast should have recommendations"
        );
    }
}