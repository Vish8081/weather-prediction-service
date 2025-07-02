package com.example.weather.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DailyForecast {
    private LocalDate date;
    private double highTemp;
    private double lowTemp;
    private double windSpeed;
    private String weatherCondition;
    private List<String> recommendations;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(double highTemp) {
        this.highTemp = highTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(double lowTemp) {
        this.lowTemp = lowTemp;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public DailyForecast(LocalDate date) {
        this.date = date;
        this.highTemp = Double.MIN_VALUE;
        this.lowTemp = Double.MAX_VALUE;
        this.recommendations = new ArrayList<>();
    }

    public void updateTemperatures(double currentTemp) {
        this.highTemp = Math.max(highTemp, currentTemp);
        this.lowTemp = Math.min(lowTemp, currentTemp);
    }

    public void updateWindSpeed(double windSpeed) {
        this.windSpeed = Math.max(this.windSpeed, windSpeed);
    }

    public void updateWeatherConditions(String condition) {
        this.weatherCondition = condition;
    }

    public void generateRecommendations() {
        recommendations.clear();

        if (weatherCondition.equalsIgnoreCase("Rain")) {
            recommendations.add("Carry umbrella");
        }

        if (highTemp > 40) {
            recommendations.add("Use sunscreen lotion");
        }

        if (windSpeed > 10) {
            recommendations.add("It's too windy, watch out!");
        }

        if (weatherCondition.equalsIgnoreCase("Thunderstorm")) {
            recommendations.add("Don't step out! A Storm is brewing!");
        }
    }
}