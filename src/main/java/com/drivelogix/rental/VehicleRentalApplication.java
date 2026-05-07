package com.drivelogix.rental;

import com.drivelogix.rental.ui.VehicleRentalSwingApp;
import javax.swing.SwingUtilities;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class VehicleRentalApplication {

    public static void main(String[] args) {
        boolean backendEnabled = getBooleanConfig("APP_BACKEND_ENABLED", "app.backend.enabled", true);
        boolean swingEnabled = getBooleanConfig("APP_SWING_ENABLED", "app.swing.enabled", true);
        String apiBaseUrl = getStringConfig("APP_API_BASE_URL", "app.api.base-url", "http://localhost:8080");

        if (backendEnabled) {
            new SpringApplicationBuilder(VehicleRentalApplication.class)
                    .headless(false)
                    .run(args);
        }

        if (swingEnabled) {
            SwingUtilities.invokeLater(() -> new VehicleRentalSwingApp(apiBaseUrl).show());
        }
    }

    private static boolean getBooleanConfig(String envKey, String propertyKey, boolean defaultValue) {
        String value = getStringConfig(envKey, propertyKey, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    private static String getStringConfig(String envKey, String propertyKey, String defaultValue) {
        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }
}
