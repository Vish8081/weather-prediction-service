package com.example.weather.service;

import com.example.weather.model.DailyForecast;
import com.example.weather.model.WeatherForecastResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = "d2929e9483efc82c82c32ee7e02d563e";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = (restTemplateBuilder != null) ? restTemplateBuilder.build() : new RestTemplate();
        this.objectMapper = (objectMapper != null) ? objectMapper : new ObjectMapper();
    }

    public WeatherForecastResponse getWeatherForecast(String city, boolean offline) {
        if (offline) {
            return getOfflineForecast(city);
        }
        try {
            System.out.println("City Parameter: " + city);
            String url = String.format("%s?q=%s&appid=%s&cnt=24&units=metric", WEATHER_API_URL, city, API_KEY);
            String response = restTemplate.getForObject(url, String.class);

            // Check if the response is valid
            if (response == null || response.contains("\"cod\":\"404\"")) {
                WeatherForecastResponse offlineResponse = getOfflineForecast(city);
                setErrorForForecasts(offlineResponse.getForecasts(), "City not found: " + city);
                return offlineResponse;
            }

            System.out.println("City Parameter: " + city);
            System.out.println("response" + response);

            return parseApiResponse(response);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error fetching weather data: " + e.getMessage());

            // Create offline forecast with error
            WeatherForecastResponse offlineResponse = getOfflineForecast(city);

            // Set error message if city not found
            if (e.getMessage() != null && e.getMessage().contains("city not found")) {
                setErrorForForecasts(offlineResponse.getForecasts(), "City not found: " + city);
            }
            return offlineResponse;
        }
    }

    private void setErrorForForecasts(List<DailyForecast> forecasts, String errorMessage) {
        if (forecasts != null) {
            for (DailyForecast forecast : forecasts) {
                forecast.setError(errorMessage);
            }
        }
    }

    private WeatherForecastResponse parseApiResponse(String jsonResponse) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode list = root.path("list");

        Map<LocalDate, DailyForecast> forecasts = new HashMap<>();

        for (JsonNode item : list) {
            LocalDate date = Instant.ofEpochSecond(item.path("dt").asLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            DailyForecast daily = forecasts.computeIfAbsent(date, DailyForecast::new);

            double slotMin = item.path("main").path("temp_min").asDouble();
            double slotMax = item.path("main").path("temp_max").asDouble();
            daily.updateTemperatures(slotMin, slotMax);          // <<< uses extremes

            double wind = item.path("wind").path("speed").asDouble();
            daily.updateWindSpeed(wind);

            String cond = item.path("weather").get(0).path("main").asText();
            daily.updateWeatherConditions(cond);
        }

        // Get next 3 days forecasts
        List<DailyForecast> next3Days = forecasts.values().stream()
                .sorted(Comparator.comparing(DailyForecast::getDate))
                .limit(3)
                .collect(Collectors.toList());

        return new WeatherForecastResponse(next3Days);
    }

    private WeatherForecastResponse getOfflineForecast(String city) {
        List<DailyForecast> mockForecasts = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 3; i++) {
            LocalDate date = today.plusDays(i);
            DailyForecast forecast = new DailyForecast(date);

            // Generate some mock data
            forecast.setHighTemp(25 + i * 3);
            forecast.setLowTemp(15 + i * 2);
            forecast.setWindSpeed(5 + i);
            forecast.setWeatherCondition(i % 2 == 0 ? "Clear" : "Clouds");

            mockForecasts.add(forecast);
        }

        return new WeatherForecastResponse(mockForecasts);
    }
}