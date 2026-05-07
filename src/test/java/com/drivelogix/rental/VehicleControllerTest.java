package com.drivelogix.rental;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drivelogix.rental.model.Rental;
import com.drivelogix.rental.model.Vehicle;
import com.drivelogix.rental.model.VehicleStatus;
import com.drivelogix.rental.repository.RentalRepository;
import com.drivelogix.rental.repository.VehicleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    void shouldFilterAvailableVehiclesByClass() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("class", "SUV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].class", is("SUV")));
    }

    @Test
    void shouldPreviewWeeklyPricingDiscount() throws Exception {
        String payload = """
                {
                  "customerName": "Alex",
                  "startDate": "2099-01-01",
                  "endDate": "2099-01-10"
                }
                """;

        mockMvc.perform(post("/api/vehicles/1/pricing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalDays", is(9)))
                .andExpect(jsonPath("$.pricingTier", is("LONG_TERM_WEEKLY")))
                .andExpect(jsonPath("$.totalPrice", is(567.00)));
    }

    @Test
    void shouldRentVehicleAndHideItFromAvailabilityList() throws Exception {
        String payload = """
                {
                  "customerName": "Jamie",
                  "startDate": "2099-02-01",
                  "endDate": "2099-02-05"
                }
                """;

        mockMvc.perform(post("/api/vehicles/2/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numberPlate", is("34-ABC-102")))
                .andExpect(jsonPath("$.rentalDays", is(4)));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 2)]").doesNotExist());

        mockMvc.perform(get("/api/vehicles/rented"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number_plate", is("34-ABC-102")))
                .andExpect(jsonPath("$[0].customerName", is("Jamie")));
    }

    @Test
    void shouldReturnVehicleOneDayAfterRentalEnds() throws Exception {
        Vehicle vehicle = vehicleRepository.findById(3L).orElseThrow();
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);

        Rental rental = rentalRepository.save(new Rental(
                vehicle,
                "Taylor",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 6),
                new BigDecimal("720.00"),
                6
        ));

        mockMvc.perform(post("/api/vehicles/rentals/" + rental.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberPlate", is("34-ABC-103")));
    }
}
