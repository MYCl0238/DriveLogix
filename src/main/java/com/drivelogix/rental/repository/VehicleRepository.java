package com.drivelogix.rental.repository;

import com.drivelogix.rental.model.Vehicle;
import com.drivelogix.rental.model.VehicleStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByNumberPlate(String numberPlate);

    @Query("""
            select v
            from Vehicle v
            where v.status = :status
              and (:brand = '' or lower(v.brand) like lower(concat('%', :brand, '%')))
              and (:model = '' or lower(v.model) like lower(concat('%', :model, '%')))
              and (:vehicleClass = '' or lower(v.vehicleClass) = lower(:vehicleClass))
              and (:maxPrice is null or v.price <= :maxPrice)
            order by v.brand, v.model
            """)
    List<Vehicle> findAvailableVehicles(
            @Param("status") VehicleStatus status,
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("vehicleClass") String vehicleClass,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
