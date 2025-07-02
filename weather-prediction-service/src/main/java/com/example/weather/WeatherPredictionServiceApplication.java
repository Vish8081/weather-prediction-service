package com.example.weather;  // Replace with your actual package name

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
public class WeatherPredictionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherPredictionServiceApplication.class, args);
	}

	// Optional: Customize Swagger/OpenAPI documentation
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Weather Prediction Service API")
						.version("1.0")
						.description("API for 3-day weather forecasts with recommendations"));
	}
}