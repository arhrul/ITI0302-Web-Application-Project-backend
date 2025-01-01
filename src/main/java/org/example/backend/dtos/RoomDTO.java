package org.example.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.RoomType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    @Schema(description = "The unique identifier of the room", example = "1")
    private Long id;

    @Schema(description = "The room number", example = "101")
    @NotBlank(message = "Room number cannot be blank")
    @Pattern(regexp = "\\d{1,4}", message = "Room number must be between 1 to 4 digits")
    private String roomNumber;

    @Schema(description = "The price per night for the room", example = "150.00")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 6, fraction = 2, message = "Price must be a valid monetary value with up to 2 decimal places")
    private Double price;

    @Schema(description = "Indicates whether the room is available", example = "true")
    private boolean available;

    @Schema(description = "The type of room (e.g., SINGLE, DOUBLE)", example = "SINGLE")
    @NotNull(message = "Room type cannot be null")
    private RoomType type;

    @Schema(description = "A brief description of the room", example = "A comfortable single room with modern amenities.")
    private String description;
}
