package com.drivelogix.rental.ui;

public record RentalPayload(
        String customerName,
        String startDate,
        String endDate
) {
}
