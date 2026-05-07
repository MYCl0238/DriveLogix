package com.drivelogix.rental.service;

import com.drivelogix.rental.dto.PricingResponse;
import com.drivelogix.rental.model.Vehicle;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    public PricingResponse calculatePricing(Vehicle vehicle, int rentalDays) {
        BigDecimal multiplier;
        String pricingTier;

        if (rentalDays >= 30) {
            multiplier = new BigDecimal("0.80");
            pricingTier = "LONG_TERM_30_PLUS";
        } else if (rentalDays >= 7) {
            multiplier = new BigDecimal("0.90");
            pricingTier = "LONG_TERM_WEEKLY";
        } else {
            multiplier = BigDecimal.ONE;
            pricingTier = "SHORT_TERM";
        }

        BigDecimal totalPrice = vehicle.getPrice()
                .multiply(multiplier)
                .multiply(BigDecimal.valueOf(rentalDays))
                .setScale(2, RoundingMode.HALF_UP);

        return new PricingResponse(
                vehicle.getId(),
                rentalDays,
                vehicle.getPrice(),
                multiplier,
                totalPrice,
                pricingTier
        );
    }
}
