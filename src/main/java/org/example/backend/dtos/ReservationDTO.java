package org.example.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    @Schema(description = "The unique identifier of the reservation", example = "1")
    private Long id;

    @Schema(description = "The date when the client checks in", example = "2024-12-01")
    @NotNull(message = "Check-in date cannot be null")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @Schema(description = "The date when the client checks out", example = "2024-12-07")
    @NotNull(message = "Check-out date cannot be null")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Schema(description = "The total number of guests for the reservation", example = "2")
    @NotNull(message = "Number of guests cannot be null")
    @Min(value = 1, message = "There must be at least one guest")
    @Max(value = 5, message = "Number of guests cannot exceed 5")
    private Long numberOfGuests;

    @Schema(description = "The total price for the reservation", example = "500.00")
    @PositiveOrZero(message = "Total price must be positive or zero")
    private Double totalPrice;

    @Schema(description = "The status of the reservation (e.g., 'available', 'canceled')", example = "available")
    private String status;

    @Schema(description = "The ID of the client who made the reservation", example = "1")
    @NotNull(message = "Client ID cannot be null")
    private Long clientId;

    @Schema(description = "The ID of the room reserved", example = "101")
    @NotNull(message = "Room ID cannot be null")
    private Long roomId;
}
