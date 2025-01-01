package org.example.backend.test.service.room;

import org.example.backend.BaseTests;
import org.example.backend.dtos.AmenityDTO;
import org.example.backend.exception.exceptions.AmenityNameAlreadyExistsException;
import org.example.backend.exception.exceptions.NoSuchAmenityException;
import org.example.backend.model.Amenity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTests extends BaseTests {

    @Test
    void createAmenitySuccessfully() {
        given(amenityRepository.save(amenityFreeWifi)).willReturn(amenityFreeWifi);
        given(amenityMapper.toAmenity(amenityDTOFreeWifi)).willReturn(amenityFreeWifi);
        given(amenityMapper.toAmenityDTO(amenityFreeWifi)).willReturn(amenityDTOFreeWifi);

        AmenityDTO result = amenityService.createAmenity(amenityDTOFreeWifi);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Free WiFi");
        then(amenityRepository).should().save(amenityFreeWifi);
    }

    @Test
    void createAmenity_NameAlreadyExists_ShouldThrowException() {
        given(amenityRepository.existsByName("Free WiFi")).willReturn(true);

        Throwable thrown = catchThrowable(() -> amenityService.createAmenity(amenityDTOFreeWifi));

        assertThat(thrown).isInstanceOf(AmenityNameAlreadyExistsException.class);
    }

    @Test
    void getAmenityByIdSuccessfully() {
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));
        given(amenityMapper.toAmenityDTO(amenityFreeWifi)).willReturn(amenityDTOFreeWifi);

        AmenityDTO result = amenityService.getAmenity(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Free WiFi");
    }

    @Test
    void getAmenityById_shouldThrowExceptionAmenityNotFound() {
        given(amenityRepository.findById(1L)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> amenityService.getAmenity(1L));

        assertThat(thrown).isInstanceOf(NoSuchAmenityException.class);
    }

    @Test
    void getAllAmenities() {
        List<Amenity> amenityList = List.of(amenityFreeWifi, amenityFreeWifi);
        given(amenityRepository.findAll()).willReturn(amenityList);
        given(amenityMapper.toAmenityDTOList(amenityList)).willReturn(List.of(amenityDTOFreeWifi, amenityDTOBreakfast));

        List<AmenityDTO> amenityDTOS = amenityService.getAllAmenities();

        assertThat(amenityDTOS).hasSize(2);
        assertThat(amenityDTOS.get(0).getName()).isEqualTo("Free WiFi");
        assertThat(amenityDTOS.get(1).getName()).isEqualTo("Breakfast");
    }

    @Test
    void updateAmenity_Successfully() {
        amenityDTOFreeWifi.setAdditionalCost(10.0);
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));
        given(amenityRepository.save(amenityFreeWifi)).willReturn(amenityFreeWifi);
        given(amenityMapper.toAmenityDTO(amenityFreeWifi)).willReturn(amenityDTOFreeWifi);

        AmenityDTO updatedAmenityDTO = amenityService.updateAmenity(1L, amenityDTOFreeWifi);

        assertThat(updatedAmenityDTO).isNotNull();
        assertThat(updatedAmenityDTO.getAdditionalCost()).isEqualTo(10.0);
        then(amenityRepository).should().save(amenityFreeWifi);
    }

    @Test
    void shouldUpdateRoomPriceWhenAmenityUpdated() {
        room.setPrice(150.00);
        amenityFreeWifi.setRooms(Collections.singletonList(room));

        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));
        given(amenityRepository.save(amenityFreeWifi)).willReturn(amenityFreeWifi);
        given(amenityMapper.toAmenityDTO(amenityFreeWifi)).willReturn(amenityDTOFreeWifi);
        given(roomRepository.save(room)).willReturn(room);

        amenityDTOFreeWifi.setAdditionalCost(10.00);

        AmenityDTO updatedAmenityDTO = amenityService.updateAmenity(1L, amenityDTOFreeWifi);

        double expectedRoomPrice = 150.00 - 0.00 + 10.00;
        assertThat(updatedAmenityDTO).isNotNull();
        assertThat(room.getPrice()).isEqualTo(expectedRoomPrice);
        verify(roomRepository).save(room);
    }

    @Test
    void updateAmenity_NameAlreadyExists() {
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));
        given(amenityRepository.existsByName("Breakfast")).willReturn(true);

        Throwable thrown = catchThrowable(() -> amenityService.updateAmenity(1L, amenityDTOBreakfast));

        assertThat(thrown).isInstanceOf(AmenityNameAlreadyExistsException.class);
    }

    @Test
    void deleteAmenitySuccessfully() {
        given(amenityRepository.findById(1L)).willReturn(Optional.of(amenityFreeWifi));

        amenityService.deleteAmenity(1L);

        verify(amenityRepository).delete(amenityFreeWifi);
    }

    @Test
    void deleteAmenity_ShouldThrowExceptionNotFound() {
        given(amenityRepository.findById(1L)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> amenityService.deleteAmenity(1L));

        assertThat(thrown).isInstanceOf(NoSuchAmenityException.class);
    }

}
