package org.example.backend.test.service.room;

import org.example.backend.BaseTests;

import org.example.backend.dtos.AmenityDTO;
import org.example.backend.exception.exceptions.AmenityAlreadyAssignedException;
import org.example.backend.exception.exceptions.NoSuchAmenityException;
import org.example.backend.exception.exceptions.NoSuchRoomException;
import org.example.backend.model.Amenity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class RoomAmenityServiceTests extends BaseTests {

    @Test
    void shouldAssignAmenityToRoomSuccessfully() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));

        room.setAmenities(new ArrayList<>());
        roomAmenityService.assignAmenityToRoom(1L, 1L);

        assertThat(room.getAmenities()).contains(amenityFreeWifi);
        assertThat(room.getPrice()).isEqualTo(150.00 + 0.00);
        then(roomRepository).should().save(room);
    }

    @Test
    void shouldThrowAmenityAlreadyAssignedExceptionWhenAmenityAlreadyAssigned() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));

        room.setAmenities(new ArrayList<>());
        roomAmenityService.assignAmenityToRoom(1L, 1L);

        Throwable thrown = catchThrowable(() -> roomAmenityService.assignAmenityToRoom(1L, 1L));

        assertThat(thrown).isInstanceOf(AmenityAlreadyAssignedException.class);
    }

    @Test
    void shouldGetAmenitiesForRoomSuccessfully() {
        List<Amenity> amenities = new ArrayList<>();
        amenities.add(amenityFreeWifi);
        amenities.add(amenityBreakfast);
        room.setAmenities(amenities);

        given(roomRepository.findById(1L)).willReturn(Optional.of(room));

        List<AmenityDTO> amenityDTOList = List.of(amenityDTOFreeWifi, amenityDTOBreakfast);
        given(amenityMapper.toAmenityDTOList(amenities)).willReturn(amenityDTOList);

        List<AmenityDTO> result = roomAmenityService.getAmenitiesByRoom(1L);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(amenityDTOFreeWifi, amenityDTOBreakfast);
    }

    @Test
    void shouldRemoveAmenityFromRoomSuccessfully() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));
        given(amenityRepository.findById(2L)).willReturn(Optional.of(amenityBreakfast));

        room.setAmenities(new ArrayList<>());
        roomAmenityService.assignAmenityToRoom(1L, 1L);
        roomAmenityService.assignAmenityToRoom(1L, 2L);

        roomAmenityService.removeAmenityFromRoom(1L, 1L);

        assertThat(room.getAmenities()).doesNotContain(amenityFreeWifi);
        assertThat(room.getPrice()).isEqualTo(170.0);
    }

    @Test
    void shouldThrowNoSuchAmenityExceptionWhenRemovingNonExistingAmenity() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));

        room.setAmenities(new ArrayList<>());
        roomAmenityService.assignAmenityToRoom(1L, 1L);
        roomAmenityService.removeAmenityFromRoom(1L, 1L);

        Throwable thrown = catchThrowable(() -> roomAmenityService.removeAmenityFromRoom(1L, 1L));

        assertThat(thrown).isInstanceOf(NoSuchAmenityException.class);
    }

    @Test
    void shouldThrowNoSuchRoomExceptionWhenRoomNotFound() {
        given(roomRepository.findById(1L)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> roomAmenityService.assignAmenityToRoom(1L, 1L));

        assertThat(thrown).isInstanceOf(NoSuchRoomException.class);
    }

    @Test
    void shouldThrowNoSuchAmenityExceptionWhenAmenityNotFound() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(amenityRepository.findById(1L)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> roomAmenityService.assignAmenityToRoom(1L, 1L));

        assertThat(thrown).isInstanceOf(NoSuchAmenityException.class);
    }
}
