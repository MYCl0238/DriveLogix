package com.drivelogix.rental.config;

import com.drivelogix.rental.model.Vehicle;
import com.drivelogix.rental.repository.VehicleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("h2")
public class DataSeeder {

    @Bean
    CommandLineRunner seedVehicles(VehicleRepository vehicleRepository) {
        return args -> {
            if (vehicleRepository.count() > 0) {
                return;
            }

            vehicleRepository.saveAll(List.of(
                    new Vehicle("34-ABC-101", "Toyota", "Corolla", "Sedan", new BigDecimal("70.00")),
                    new Vehicle("34-ABC-102", "Honda", "Civic", "Sedan", new BigDecimal("72.50")),
                    new Vehicle("34-ABC-103", "BMW", "X3", "SUV", new BigDecimal("120.00")),
                    new Vehicle("34-ABC-104", "Nissan", "Qashqai", "SUV", new BigDecimal("95.00")),
                    new Vehicle("34-ABC-105", "Mercedes", "Vito", "Van", new BigDecimal("140.00")),
                    new Vehicle("34-ABC-106", "Renault", "Clio", "Hatchback", new BigDecimal("55.00"))
            ));
        };
    }
}
