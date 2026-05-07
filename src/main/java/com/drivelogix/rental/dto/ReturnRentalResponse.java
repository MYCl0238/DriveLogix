package com.drivelogix.rental.dto;

import java.time.LocalDate;

public record ReturnRentalResponse(
        Long rentalId,
        Long vehicleId,
        String numberPlate,
        LocalDate returnedAt,
        String message
) {
}
