package com.drivelogix.rental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.drivelogix.rental.model.Rental;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RentedVehicleResponse(
        Long rentalId,
        Long vehicleId,
        @JsonProperty("number_plate") String numberPlate,
        String brand,
        String model,
        @JsonProperty("class") String vehicleClass,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate eligibleReturnDate,
        int rentalDays,
        BigDecimal totalPrice,
        boolean canReturn
) {
    public static RentedVehicleResponse from(Rental rental) {
        LocalDate eligibleReturnDate = rental.getEndDate().plusDays(1);
        return new RentedVehicleResponse(
                rental.getId(),
                rental.getVehicle().getId(),
                rental.getVehicle().getNumberPlate(),
                rental.getVehicle().getBrand(),
                rental.getVehicle().getModel(),
                rental.getVehicle().getVehicleClass(),
                rental.getCustomerName(),
                rental.getStartDate(),
                rental.getEndDate(),
                eligibleReturnDate,
                rental.getRentalDays(),
                rental.getTotalPrice(),
                !LocalDate.now().isBefore(eligibleReturnDate)
        );
    }
}
