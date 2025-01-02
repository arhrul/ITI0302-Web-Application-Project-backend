package org.example.backend.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.ReservationDTO;
import org.example.backend.service.reservation.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
@Tag(name = "Reservation", description = "Operations related to reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Create a new reservation and return the reservation details")
    @ApiResponse(responseCode = "200", description = "Reservation created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reservation data")
    public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody @Valid @Parameter(description = "Reservation details") ReservationDTO reservationDTO) {
        log.info("Received request to create a reservation: {}", reservationDTO);
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
        log.info("Reservation created successfully: {}", createdReservation);
        return ResponseEntity.ok(createdReservation);    }

    @GetMapping
    @Operation(summary = "Get list of reservations", description = "Retrieve a list of all reservations")
    @ApiResponse(responseCode = "200", description = "List of reservations")
    public ResponseEntity<List<ReservationDTO>> getReservations() {
        log.info("Fetching all reservations");
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        log.info("Found {} reservations", reservations.size());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get reservations by client ID", description = "Retrieve a list of reservations for a specific client")
    @ApiResponse(responseCode = "200", description = "List of reservations for the client")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<List<ReservationDTO>> getReservationsByClientId(
            @PathVariable @Parameter(description = "Client ID") Long clientId) {
        log.info("Fetching reservations for client ID: {}", clientId);
        List<ReservationDTO> reservations = reservationService.getReservationsByClientId(clientId);
        log.info("Found {} reservations for client ID: {}", reservations.size(), clientId);
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation details", description = "Update the details of a specific reservation")
    @ApiResponse(responseCode = "200", description = "Reservation updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid reservation data")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable @Parameter(description = "Reservation ID") Long id,
            @RequestBody @Valid @Parameter(description = "Updated reservation details") ReservationDTO updatedReservationDTO) {
        log.info("Updating reservation with ID: {}. New data: {}", id, updatedReservationDTO);
        ReservationDTO updatedReservation = reservationService.updateReservation(id, updatedReservationDTO);
        log.info("Reservation updated successfully: {}", updatedReservation);
        return ResponseEntity.ok(updatedReservation);    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation", description = "Delete a reservation by its unique ID")
    @ApiResponse(responseCode = "204", description = "Reservation deleted successfully")
    @ApiResponse(responseCode = "404", description = "Reservation not found")
    public ResponseEntity<Void> deleteReservation(@PathVariable @Parameter(description = "Reservation ID") Long id) {
        log.info("Deleting reservation with ID: {}", id);
        reservationService.deleteReservation(id);
        log.info("Reservation with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
