package com.weatherapp.weather_app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Scanner;

public class WeatherApp {

    // OpenWeatherMap API key (Replace this with your own key)
    private static final String API_KEY = "849607e424e186f89795d81ea5b86015";
    
    // API URL format (Notice that %s will be replaced by the encoded city name)
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);        
        
        System.out.println("Enter the city name: ");
        String city = scanner.nextLine();
        
        // Append the country code for India
        String cityWithCountry = city + ",IN"; 

        try {
            // Fetch weather data using the updated city with country code
            String weatherData = getWeatherData(cityWithCountry);
            if (weatherData != null) {
                // Parse and display the weather data
                parseAndDisplayWeatherData(weatherData);
            } else {
                System.out.println("Failed to retrieve weather data.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        scanner.close();
    }

    // Method to send an HTTP GET request and fetch weather data from OpenWeatherMap API
    public static String getWeatherData(String city) throws IOException {
        // URL-encode the city name to handle spaces or special characters
        String encodedCity = URLEncoder.encode(city, "UTF-8");

        // Build the full URL
        String url = String.format(API_URL, encodedCity, API_KEY);
        
        // Create HTTP client and make the GET request
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        
        try {
            HttpResponse response = client.execute(request);
            String json = EntityUtils.toString(response.getEntity());
            client.close();
            return json;
        } catch (ClientProtocolException e) {
            throw new IOException("HTTP request failed", e);
        }
    }

    // Method to parse the JSON response and display weather details
    public static void parseAndDisplayWeatherData(String weatherData) {
        try {
            // Create an ObjectMapper to map the JSON response to a JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(weatherData);

            // Extract the main information from the JSON response
            JsonNode mainNode = rootNode.path("main");
            double temperature = mainNode.path("temp").asDouble();
            int humidity = mainNode.path("humidity").asInt();

            // Extract the weather description
            JsonNode weatherNode = rootNode.path("weather").get(0);
            String description = weatherNode.path("description").asText();

            // Display weather information
            System.out.println("Weather in " + rootNode.path("name").asText() + ":");
            System.out.println("Temperature: " + temperature + "°C");
            System.out.println("Humidity: " + humidity + "%");
            System.out.println("Condition: " + description);
        } catch (Exception e) {
            System.out.println("Failed to parse weather data: " + e.getMessage());
        }
    }
}

