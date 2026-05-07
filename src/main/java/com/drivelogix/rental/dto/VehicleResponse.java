package com.drivelogix.rental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.drivelogix.rental.model.Vehicle;
import com.drivelogix.rental.model.VehicleStatus;
import java.math.BigDecimal;

public record VehicleResponse(
        Long id,
        @JsonProperty("number_plate") String numberPlate,
        String brand,
        String model,
        @JsonProperty("class") String vehicleClass,
        BigDecimal price,
        VehicleStatus status
) {
    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getNumberPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getVehicleClass(),
                vehicle.getPrice(),
                vehicle.getStatus()
        );
    }
}
