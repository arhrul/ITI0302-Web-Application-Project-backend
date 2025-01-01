package org.example.backend.test.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.enums.RoomType;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoomDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void shouldValidateRoomDTO() {
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber("101")
                .price(150.00)
                .available(true)
                .type(RoomType.SINGLE)
                .description("A comfortable single room with modern amenities.")
                .build();

        Set<ConstraintViolation<RoomDTO>> violations = validator.validate(roomDTO);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenRoomNumberIsBlank() {
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber("  ")
                .price(150.00)
                .available(true)
                .type(RoomType.SINGLE)
                .description("A comfortable room.")
                .build();

        Set<ConstraintViolation<RoomDTO>> violations = validator.validate(roomDTO);

        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("Room number cannot be blank"));
    }

    @Test
    void shouldFailWhenRoomNumberIsInvalid() {
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber("12345")
                .price(150.00)
                .available(true)
                .type(RoomType.SINGLE)
                .description("A comfortable room.")
                .build();

        Set<ConstraintViolation<RoomDTO>> violations = validator.validate(roomDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Room number must be between 1 to 4 digits");
    }

    @Test
    void shouldFailWhenPriceIsNull() {
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber("101")
                .price(null)
                .available(true)
                .type(RoomType.SINGLE)
                .description("A comfortable room.")
                .build();

        Set<ConstraintViolation<RoomDTO>> violations = validator.validate(roomDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price cannot be null");
    }

    @Test
    void shouldFailWhenPriceIsNegative() {
        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber("101")
                .price(-1.00)
                .available(true)
                .type(RoomType.SINGLE)
                .description("A comfortable room.")
                .build();

        Set<ConstraintViolation<RoomDTO>> violations = validator.validate(roomDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price must be greater than 0");
    }
}
