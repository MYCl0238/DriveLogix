package com.drivelogix.rental.dto;

import java.math.BigDecimal;

public record PricingResponse(
        Long vehicleId,
        int rentalDays,
        BigDecimal dailyPrice,
        BigDecimal multiplier,
        BigDecimal totalPrice,
        String pricingTier
) {
}
