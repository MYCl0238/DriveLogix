package com.drivelogix.rental.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }

    public List<VehicleDto> fetchVehicles(String brand, String model, String vehicleClass, String maxPrice) throws IOException, InterruptedException {
        List<String> queryParts = new ArrayList<>();
        addQuery(queryParts, "brand", brand);
        addQuery(queryParts, "model", model);
        addQuery(queryParts, "class", vehicleClass);
        addQuery(queryParts, "maxPrice", maxPrice);

        String query = queryParts.isEmpty() ? "" : "?" + String.join("&", queryParts);
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/vehicles" + query))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    public List<RentedVehicleDto> fetchRentedVehicles() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/vehicles/rented"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), new TypeReference<>() {
        });
    }

    public PricingDto previewPricing(Long vehicleId, RentalPayload payload) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/vehicles/" + vehicleId + "/pricing"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), PricingDto.class);
    }

    public RentalDto rentVehicle(Long vehicleId, RentalPayload payload) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/vehicles/" + vehicleId + "/rent"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), RentalDto.class);
    }

    public ReturnRentalDto returnVehicle(Long rentalId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/vehicles/rentals/" + rentalId + "/return"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return objectMapper.readValue(response.body(), ReturnRentalDto.class);
    }

    private void addQuery(List<String> queryParts, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        queryParts.add(URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value.trim(), StandardCharsets.UTF_8));
    }

    private void ensureSuccess(HttpResponse<String> response) throws IOException {
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return;
        }

        ApiError apiError;
        try {
            apiError = objectMapper.readValue(response.body(), ApiError.class);
        } catch (Exception ignored) {
            apiError = new ApiError(null, response.statusCode(), "Error", response.body());
        }
        throw new IOException(apiError.message());
    }
}
