package com.drivelogix.rental.ui;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record RentedVehicleDto(
        Long rentalId,
        Long vehicleId,
        @JsonProperty("number_plate") String numberPlate,
        String brand,
        String model,
        @JsonProperty("class") String vehicleClass,
        String customerName,
        String startDate,
        String endDate,
        String eligibleReturnDate,
        int rentalDays,
        BigDecimal totalPrice,
        boolean canReturn
) {
}
