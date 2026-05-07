package com.drivelogix.rental.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record VehicleDto(
        Long id,
        @JsonProperty("number_plate") String numberPlate,
        String brand,
        String model,
        @JsonProperty("class") String vehicleClass,
        BigDecimal price,
        String status
) {
}
