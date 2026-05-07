package com.drivelogix.rental.controller;

import com.drivelogix.rental.dto.PricingResponse;
import com.drivelogix.rental.dto.RentalRequest;
import com.drivelogix.rental.dto.RentalResponse;
import com.drivelogix.rental.dto.ReturnRentalResponse;
import com.drivelogix.rental.dto.RentedVehicleResponse;
import com.drivelogix.rental.dto.VehicleResponse;
import com.drivelogix.rental.service.VehicleService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<VehicleResponse> getAvailableVehicles(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false, name = "class") String vehicleClass,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return vehicleService.getAvailableVehicles(brand, model, vehicleClass, maxPrice)
                .stream()
                .map(VehicleResponse::from)
                .toList();
    }

    @GetMapping("/rented")
    public List<RentedVehicleResponse> getRentedVehicles() {
        return vehicleService.getRentedVehicles();
    }

    @PostMapping("/{vehicleId}/pricing")
    public PricingResponse previewPricing(
            @PathVariable Long vehicleId,
            @Valid @RequestBody RentalRequest request
    ) {
        return vehicleService.previewPricing(vehicleId, request);
    }

    @PostMapping("/{vehicleId}/rent")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rentVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody RentalRequest request
    ) {
        return vehicleService.rentVehicle(vehicleId, request);
    }

    @PostMapping("/rentals/{rentalId}/return")
    public ReturnRentalResponse returnVehicle(@PathVariable Long rentalId) {
        return vehicleService.returnVehicle(rentalId);
    }
}
