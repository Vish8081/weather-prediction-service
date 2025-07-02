package com.example.weather.controller;

import com.example.weather.model.WeatherForecastResponse;
import com.example.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather API", description = "Get weather predictions for cities")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get 3-day weather forecast for a city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved forecast"),
            @ApiResponse(responseCode = "400", description = "Invalid city parameter"),
            @ApiResponse(responseCode = "503", description = "Weather service unavailable")
    })

    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(
            @RequestParam String city,
            @RequestParam(required = false, defaultValue = "false") boolean offline) {

        WeatherForecastResponse response = weatherService.getWeatherForecast(city, offline);
        return ResponseEntity.ok(response);
    }
}