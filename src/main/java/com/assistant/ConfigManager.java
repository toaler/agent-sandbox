package com.assistant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + CONFIG_FILE, e);
        }
    }

    public static String getOpenWeatherApiKey() {
        return getProperty("openweather.api.key");
    }

    public static String getGoogleMapsApiKey() {
        return getProperty("google.maps.api.key");
    }

    private static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing required property: " + key);
        }
        return value;
    }
} 