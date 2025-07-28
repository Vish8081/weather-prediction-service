package com.example.weather.controller;

import com.example.weather.model.WeatherForecastResponse;
import com.example.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@Tag(name = "Weather API", description = "Provides 3-day weather forecasts with personalized recommendations")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/forecast")
    @Operation(
            summary = "Get 3-day weather forecast",
            description = "Returns weather forecast for the next 3 days including temperature ranges, wind speed, weather conditions, and personalized recommendations.",
            parameters = {
                    @Parameter(
                            name = "city",
                            description = "Name of the city to get weather for",
                            example = "London",
                            required = true
                    ),
                    @Parameter(
                            name = "offline",
                            description = "Use offline mock data instead of real API (for testing/demo purposes)",
                            example = "false"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved forecast",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WeatherForecastResponse.class),
                            examples = @ExampleObject(value = """
                    {
                        "forecasts": [
                            {
                                "date": "2023-07-10",
                                "highTemp": 25.5,
                                "lowTemp": 18.2,
                                "windSpeed": 12.3,
                                "weatherCondition": "Rain",
                                "recommendations": [
                                    "Carry umbrella",
                                    "It's too windy, watch out!"
                                ]
                            }
                        ]
                    }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                        "timestamp": "2023-07-07T12:34:56.789+00:00",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Required parameter 'city' is not present",
                        "path": "/api/weather/forecast"
                    }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Weather service unavailable",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                    {
                        "timestamp": "2023-07-07T12:34:56.789+00:00",
                        "status": 503,
                        "error": "Service Unavailable",
                        "message": "Weather API service is currently unavailable",
                        "path": "/api/weather/forecast"
                    }
                """)
                    )
            )
    })
    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(
            @RequestParam String city,
            @RequestParam(required = false, defaultValue = "false") boolean offline) {

        WeatherForecastResponse response = weatherService.getWeatherForecast(city, offline);
        return ResponseEntity.ok(response);
    }
}