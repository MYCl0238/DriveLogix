package com.drivelogix.rental.ui;

import java.math.BigDecimal;

public record RentalDto(
        Long rentalId,
        Long vehicleId,
        String numberPlate,
        String customerName,
        String startDate,
        String endDate,
        int rentalDays,
        BigDecimal totalPrice
) {
}
