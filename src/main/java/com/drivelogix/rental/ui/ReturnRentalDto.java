package com.drivelogix.rental.ui;

public record ReturnRentalDto(
        Long rentalId,
        Long vehicleId,
        String numberPlate,
        String returnedAt,
        String message
) {
}
