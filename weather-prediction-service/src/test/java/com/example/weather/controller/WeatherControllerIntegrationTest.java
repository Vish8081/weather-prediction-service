package com.example.weather.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWeatherForecast_ValidCity_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/weather/forecast?city=London"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forecasts").isArray());
    }

    @Test
    void getWeatherForecast_MissingCity_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/weather/forecast"))
                .andExpect(status().isBadRequest());
    }
}