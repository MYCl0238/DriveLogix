package com.drivelogix.rental.service;

import com.drivelogix.rental.dto.PricingResponse;
import com.drivelogix.rental.dto.RentalRequest;
import com.drivelogix.rental.dto.RentalResponse;
import com.drivelogix.rental.dto.ReturnRentalResponse;
import com.drivelogix.rental.dto.RentedVehicleResponse;
import com.drivelogix.rental.exception.BusinessException;
import com.drivelogix.rental.exception.NotFoundException;
import com.drivelogix.rental.model.Rental;
import com.drivelogix.rental.model.Vehicle;
import com.drivelogix.rental.model.VehicleStatus;
import com.drivelogix.rental.repository.RentalRepository;
import com.drivelogix.rental.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final PricingService pricingService;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, PricingService pricingService) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.pricingService = pricingService;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles(String brand, String model, String vehicleClass, BigDecimal maxPrice) {
        return vehicleRepository.findAvailableVehicles(
                VehicleStatus.AVAILABLE,
                normalize(brand),
                normalize(model),
                normalize(vehicleClass),
                maxPrice
        );
    }

    @Transactional(readOnly = true)
    public PricingResponse previewPricing(Long vehicleId, RentalRequest request) {
        Vehicle vehicle = getAvailableVehicle(vehicleId);
        int rentalDays = validateAndGetRentalDays(request);
        return pricingService.calculatePricing(vehicle, rentalDays);
    }

    @Transactional(readOnly = true)
    public List<RentedVehicleResponse> getRentedVehicles() {
        return rentalRepository.findAllWithVehicleOrderByStartDateDesc()
                .stream()
                .map(RentedVehicleResponse::from)
                .toList();
    }

    @Transactional
    public RentalResponse rentVehicle(Long vehicleId, RentalRequest request) {
        Vehicle vehicle = getAvailableVehicle(vehicleId);
        int rentalDays = validateAndGetRentalDays(request);
        PricingResponse pricing = pricingService.calculatePricing(vehicle, rentalDays);

        vehicle.setStatus(VehicleStatus.RENTED);

        Rental rental = new Rental(
                vehicle,
                request.customerName().trim(),
                request.startDate(),
                request.endDate(),
                pricing.totalPrice(),
                rentalDays
        );

        Rental savedRental = rentalRepository.save(rental);
        vehicleRepository.save(vehicle);

        return new RentalResponse(
                savedRental.getId(),
                vehicle.getId(),
                vehicle.getNumberPlate(),
                savedRental.getCustomerName(),
                savedRental.getStartDate(),
                savedRental.getEndDate(),
                savedRental.getRentalDays(),
                savedRental.getTotalPrice()
        );
    }

    @Transactional
    public ReturnRentalResponse returnVehicle(Long rentalId) {
        Rental rental = rentalRepository.findByIdWithVehicle(rentalId)
                .orElseThrow(() -> new NotFoundException("Rental not found: " + rentalId));

        if (rental.getReturnedAt() != null) {
            throw new BusinessException("Vehicle has already been returned.");
        }

        LocalDate today = LocalDate.now();
        LocalDate eligibleReturnDate = rental.getEndDate().plusDays(1);
        if (today.isBefore(eligibleReturnDate)) {
            throw new BusinessException("Vehicle can be returned on or after " + eligibleReturnDate + ".");
        }

        rental.setReturnedAt(today);
        rental.getVehicle().setStatus(VehicleStatus.AVAILABLE);

        rentalRepository.save(rental);
        vehicleRepository.save(rental.getVehicle());

        return new ReturnRentalResponse(
                rental.getId(),
                rental.getVehicle().getId(),
                rental.getVehicle().getNumberPlate(),
                today,
                "Vehicle returned successfully and is now available."
        );
    }

    private Vehicle getAvailableVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found: " + vehicleId));

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new BusinessException("Vehicle is already rented and not available.");
        }

        return vehicle;
    }

    private int validateAndGetRentalDays(RentalRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new BusinessException("End date must be after start date.");
        }

        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        if (days <= 0) {
            throw new BusinessException("Rental period must be at least 1 day.");
        }
        if (days > Integer.MAX_VALUE) {
            throw new BusinessException("Rental duration is too large.");
        }
        return (int) days;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }
}
