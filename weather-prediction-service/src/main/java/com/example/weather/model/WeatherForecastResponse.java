package com.example.weather.model;

import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WeatherForecastResponse {
    private List<DailyForecast> forecasts;

    public List<DailyForecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<DailyForecast> forecasts) {
        this.forecasts = forecasts;
    }

    public WeatherForecastResponse(List<DailyForecast> forecasts) {
        this.forecasts = forecasts.stream()
                .peek(DailyForecast::generateRecommendations)
                .collect(Collectors.toList());
    }
}