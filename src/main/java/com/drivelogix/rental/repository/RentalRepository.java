package com.drivelogix.rental.repository;

import com.drivelogix.rental.model.Rental;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("""
            select r
            from Rental r
            join fetch r.vehicle v
            where r.returnedAt is null
            order by r.startDate desc, r.id desc
            """)
    List<Rental> findAllWithVehicleOrderByStartDateDesc();

    @Query("""
            select r
            from Rental r
            join fetch r.vehicle v
            where r.id = :rentalId
            """)
    Optional<Rental> findByIdWithVehicle(Long rentalId);
}
