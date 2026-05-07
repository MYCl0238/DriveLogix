package com.drivelogix.rental.ui;

public record ApiError(
        String timestamp,
        int status,
        String error,
        String message
) {
}
