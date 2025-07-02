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
            String url = String.format("%s?q=%s&appid=%s&cnt=10&units=metric", WEATHER_API_URL, city, API_KEY);
            String response = restTemplate.getForObject(url, String.class);
            return parseApiResponse(response);
        } catch (Exception e) {
            // Fallback to offline mode if API fails
            return getOfflineForecast(city);
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

            if (!forecasts.containsKey(date)) {
                forecasts.put(date, new DailyForecast(date));
            }

            DailyForecast dailyForecast = forecasts.get(date);
            double temp = item.path("main").path("temp").asDouble();
            double windSpeed = item.path("wind").path("speed").asDouble();
            String weatherMain = item.path("weather").get(0).path("main").asText();

            dailyForecast.updateTemperatures(temp);
            dailyForecast.updateWindSpeed(windSpeed);
            dailyForecast.updateWeatherConditions(weatherMain);
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