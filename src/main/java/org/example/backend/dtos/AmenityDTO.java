package org.example.backend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityDTO {

    @Schema(description = "The unique identifier of the amenity", example = "1")
    private Long id;

    @NotBlank(message = "Amenity name cannot be blank")
    @Size(max = 50, message = "Amenity name cannot exceed 50 characters")
    @Schema(description = "The name of the amenity", example = "Free WiFi")
    private String name;

    @Schema(description = "A brief description of the amenity", example = "High-speed wireless internet access")
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @PositiveOrZero(message = "Additional cost must be positive or zero")
    @Schema(description = "The additional cost for the amenity", example = "10.00")
    private double additionalCost;
}
