package org.example.backend.test.service.reservation;

import org.example.backend.BaseTestsSetup;
import org.example.backend.dtos.ReservationDTO;
import org.example.backend.enums.RoomType;
import org.example.backend.exception.exceptions.InvalidNumberOfGuestsException;
import org.example.backend.exception.exceptions.NoSuchReservationException;
import org.example.backend.exception.exceptions.NoSuchRoomException;
import org.example.backend.exception.exceptions.RoomAlreadyBookedException;
import org.example.backend.model.Reservation;
import org.example.backend.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests extends BaseTestsSetup {

    @Test
    void shouldCreateReservationSuccessfully() {
        given(reservationMapper.toReservation(any(ReservationDTO.class))).willReturn(reservation);
        given(clientRepository.findById(1L)).willReturn(Optional.of(client));
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));

        given(reservationRepository.save(reservation)).willReturn(reservation);
        given(reservationMapper.toReservationDto(reservation)).willReturn(reservationDTO);

        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);

        assertNotNull(createdReservation);
        assertEquals(1L, createdReservation.getClientId());
        assertEquals(1L, createdReservation.getRoomId());
        then(reservationRepository).should().save(reservation);
    }

    @Test
    void testRoomAlreadyBookedForGivenDateRange() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));

        Reservation existingReservation = new Reservation();
        existingReservation.setCheckInDate(LocalDate.of(2025, 1, 5));
        existingReservation.setCheckOutDate(LocalDate.of(2025, 1, 12));
        existingReservation.setRoomId(room);

        LocalDate checkInDate = LocalDate.of(2025, 1, 10);
        LocalDate checkOutDate = LocalDate.of(2025, 1, 15);

        when(reservationRepository.findByRoomId(room)).thenReturn(List.of(existingReservation));
        Long roomId = room.getId();

        RoomAlreadyBookedException exception = assertThrows(RoomAlreadyBookedException.class, () -> {
            reservationService.validateRoomAvailability(roomId, checkInDate, checkOutDate);
        });
        assertEquals("The room is already booked for the selected dates.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRoomNotFound() {
        given(roomRepository.findById(1L)).willReturn(java.util.Optional.empty());

        Throwable thrown = catchThrowable(() -> reservationService.getRoomById(1L));

        assertThat(thrown).isInstanceOf(NoSuchRoomException.class)
                .hasMessage("Room not found!");
    }

    @Test
    void shouldThrowExceptionWhenInvalidNumberOfGuests() {
        reservationDTO.setNumberOfGuests(3L);
        given(clientRepository.findById(1L)).willReturn(java.util.Optional.of(client));
        given(roomRepository.findById(1L)).willReturn(java.util.Optional.of(room));

        Throwable thrown = catchThrowable(() -> reservationService.createReservation(reservationDTO));

        assertThat(thrown).isInstanceOf(InvalidNumberOfGuestsException.class)
                .hasMessage("For a SINGLE room, only 1 guest is allowed.");
    }

    @Test
    void shouldFetchReservationByIdSuccessfully() {
        given(reservationRepository.findById(1L)).willReturn(java.util.Optional.of(reservation));
        given(reservationMapper.toReservationDto(reservation)).willReturn(reservationDTO);

        ReservationDTO fetchedReservation = reservationService.getReservation(1L);

        assertThat(fetchedReservation).isNotNull();
        assertThat(fetchedReservation.getId()).isEqualTo(1L);
        assertThat(fetchedReservation.getClientId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenReservationNotFound() {
        given(reservationRepository.findById(1L)).willReturn(java.util.Optional.empty());

        Throwable thrown = catchThrowable(() -> reservationService.getReservation(1L));

        assertThat(thrown).isInstanceOf(NoSuchReservationException.class)
                .hasMessage("Reservation not found!");
    }

    @Test
    void shouldUpdateReservationSuccessfully() {
        reservationDTO.setCheckInDate(LocalDate.of(2026, 12, 5));
        reservationDTO.setCheckOutDate(LocalDate.of(2026, 12, 10));
        given(reservationRepository.findById(1L)).willReturn(java.util.Optional.of(reservation));
        given(clientRepository.findById(1L)).willReturn(java.util.Optional.of(client));
        given(roomRepository.findById(1L)).willReturn(java.util.Optional.of(room));
        given(reservationRepository.save(reservation)).willReturn(reservation);
        given(reservationMapper.toReservationDto(reservation)).willReturn(reservationDTO);

        ReservationDTO updatedReservation = reservationService.updateReservation(1L, reservationDTO);

        assertThat(updatedReservation).isNotNull();
        assertThat(updatedReservation.getCheckInDate()).isEqualTo(LocalDate.of(2026, 12, 5));
        assertThat(updatedReservation.getCheckOutDate()).isEqualTo(LocalDate.of(2026, 12, 10));
    }

    @Test
    void shouldDeleteReservationSuccessfully() {
        Room room = new Room();
        room.setId(1L);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRoomId(room);

        given(reservationRepository.findById(1L)).willReturn(Optional.of(reservation));

        given(roomRepository.findById(1L)).willReturn(Optional.of(room));

        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setRoomId(1L);
        given(reservationMapper.toReservationDto(reservation)).willReturn(reservationDTO);

        reservationService.deleteReservation(1L);

        then(reservationRepository).should().delete(reservation);
    }

    @Test
    void shouldFetchAllReservationsSuccessfully() {
        given(reservationRepository.findAll()).willReturn(List.of(reservation));
        given(reservationMapper.toReservationDTOList(List.of(reservation))).willReturn(List.of(reservationDTO));

        List<ReservationDTO> reservations = reservationService.getAllReservations();

        assertThat(reservations).hasSize(1);
        assertThat(reservations.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnReservationsForClient() {
        given(clientRepository.findById(1L)).willReturn(Optional.of(client));

        Reservation reservation1 = new Reservation();
        reservation1.setClientId(client);

        Reservation reservation2 = new Reservation();
        reservation2.setClientId(client);

        when(clientRepository.findById(client.getId())).thenReturn(java.util.Optional.of(client));
        when(reservationRepository.findByClientId(client)).thenReturn(Arrays.asList(reservation1, reservation2));
        when(reservationMapper.toReservationDTOList(Arrays.asList(reservation1, reservation2)))
                .thenReturn(Arrays.asList(new ReservationDTO(), new ReservationDTO()));

        List<ReservationDTO> result = reservationService.getReservationsByClientId(client.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowExceptionWhenCheckInDateIsNotBeforeCheckOutDate() {
        LocalDate checkInDate = LocalDate.of(2025, 1, 3);
        LocalDate checkOutDate = LocalDate.of(2025, 1, 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reservationService.validateReservationDates(checkInDate, checkOutDate)
        );
        assertEquals("Check-in date must be before the check-out date.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCheckInOrCheckOutDateIsNull() {
        LocalDate date = LocalDate.of(2025, 1, 3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                reservationService.validateReservationDates(null, date)
        );
        assertEquals("Check-in date and check-out date must not be null.", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionWhenDatesAreValid() {
        assertDoesNotThrow(() -> reservationService.validateReservationDates(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3)));
    }

    @Test
    void shouldThrowExceptionForInvalidNumberOfGuests() {
        Room room = mock(Room.class);
        when(room.getType()).thenReturn(RoomType.SINGLE);

        InvalidNumberOfGuestsException exception = assertThrows(InvalidNumberOfGuestsException.class, () ->
                reservationService.validateNumberOfGuests(room, 2L)
        );
        assertEquals("For a SINGLE room, only 1 guest is allowed.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidGuestCountInDoubleRoom_LessThanOne() {
        when(room.getType()).thenReturn(RoomType.DOUBLE);

        InvalidNumberOfGuestsException exception = assertThrows(InvalidNumberOfGuestsException.class, () ->
                reservationService.validateNumberOfGuests(room, 0L)
        );
        assertEquals("There must be at least 1 guest.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidGuestCountInDoubleRoom_GreaterThanTwo() {
        Room room = mock(Room.class);
        when(room.getType()).thenReturn(RoomType.DOUBLE);

        InvalidNumberOfGuestsException exception = assertThrows(InvalidNumberOfGuestsException.class, () ->
                reservationService.validateNumberOfGuests(room, 3L)
        );
        assertEquals("For a DOUBLE room, the number of guests must be between 1 and 2.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidGuestCountInTwinRoom_GreaterThanTwo() {
        Room room = mock(Room.class);
        when(room.getType()).thenReturn(RoomType.TWIN);

        InvalidNumberOfGuestsException exception = assertThrows(InvalidNumberOfGuestsException.class, () ->
                reservationService.validateNumberOfGuests(room, 3L)
        );
        assertEquals("For a TWIN room, the number of guests must be between 1 and 2.", exception.getMessage());
    }


    @Test
    void shouldNotThrowExceptionForValidNumberOfGuests() {
        Room room = mock(Room.class);
        when(room.getType()).thenReturn(RoomType.DOUBLE);

        assertDoesNotThrow(() -> reservationService.validateNumberOfGuests(room, 2L));
    }

    @Test
    void shouldThrowExceptionForTooManyGuestsInDeluxeRoom() {
        Room room = mock(Room.class);
        when(room.getType()).thenReturn(RoomType.DELUXE);

        InvalidNumberOfGuestsException exception = assertThrows(InvalidNumberOfGuestsException.class, () ->
                reservationService.validateNumberOfGuests(room, 6L)
        );
        assertEquals("For a DELUXE room, the number of guests must be between 1 and 5.", exception.getMessage());
    }

}
