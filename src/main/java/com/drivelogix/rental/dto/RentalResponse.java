package com.drivelogix.rental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalResponse(
        Long rentalId,
        Long vehicleId,
        String numberPlate,
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        int rentalDays,
        BigDecimal totalPrice
) {
}
