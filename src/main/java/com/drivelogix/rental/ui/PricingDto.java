package com.drivelogix.rental.ui;

import java.math.BigDecimal;

public record PricingDto(
        Long vehicleId,
        int rentalDays,
        BigDecimal dailyPrice,
        BigDecimal multiplier,
        BigDecimal totalPrice,
        String pricingTier
) {
}
