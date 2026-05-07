package com.drivelogix.rental.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalRequest(
        @NotBlank String customerName,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull @Future LocalDate endDate
) {
}
