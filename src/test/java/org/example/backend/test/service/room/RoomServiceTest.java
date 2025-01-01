package org.example.backend.test.service.room;

import org.example.backend.BaseTestsSetup;
import org.example.backend.criteria.RoomSearchCriteria;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.enums.RoomType;
import org.example.backend.exception.exceptions.NoSuchRoomException;
import org.example.backend.exception.exceptions.RoomDeletionException;
import org.example.backend.exception.exceptions.RoomNumberAlreadyExistsException;
import org.example.backend.model.Reservation;
import org.example.backend.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest extends BaseTestsSetup {

    @Test
    void shouldCreateRoomSuccessfully() {
        given(roomRepository.save(room)).willReturn(room);
        given(roomMapper.toRoom(roomDTO)).willReturn(room);
        given(roomMapper.toRoomDto(room)).willReturn(roomDTO);

        RoomDTO createdRoom = roomService.createRoom(roomDTO);

        assertThat(createdRoom).isNotNull();
        assertThat(createdRoom.getRoomNumber()).isEqualTo("101");
        then(roomRepository).should().save(room);
    }

    @Test
    void shouldThrowExceptionWhenRoomNumberAlreadyExists() {
        given(roomRepository.existsByRoomNumber("101")).willReturn(true);

        Throwable thrown = catchThrowable(() -> roomService.createRoom(roomDTO));

        assertThat(thrown).isInstanceOf(RoomNumberAlreadyExistsException.class)
                .hasMessage("Room with number 101 already exists.");
    }

    @Test
    void shouldFetchRoomByIdSuccessfully() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(roomMapper.toRoomDto(room)).willReturn(roomDTO);

        RoomDTO fetchedRoom = roomService.getRoom(1L);

        assertThat(fetchedRoom).isNotNull();
        assertThat(fetchedRoom.getRoomNumber()).isEqualTo("101");
    }

    @Test
    void shouldThrowExceptionWhenRoomNotFound() {
        given(roomRepository.findById(1L)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> roomService.getRoom(1L));

        assertThat(thrown).isInstanceOf(NoSuchRoomException.class)
                .hasMessage("Room not found!");
    }

    @Test
    void shouldUpdateRoomSuccessfully() {
        roomDTO.setRoomNumber("102");
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(roomRepository.save(room)).willReturn(room);
        given(roomMapper.toRoomDto(room)).willReturn(roomDTO);

        RoomDTO updatedRoom = roomService.updateRoom(1L, roomDTO);

        assertThat(updatedRoom).isNotNull();
        assertThat(updatedRoom.getRoomNumber()).isEqualTo("102");
        then(roomRepository).should().save(room);
    }

    @Test
    void shouldDeleteRoomSuccessfully() {
        given(roomRepository.findById(1L)).willReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        then(roomRepository).should().delete(room);
    }

    @Test
    void shouldFetchAllRoomsDTO() {
        List<Room> rooms = List.of(room);
        given(roomRepository.findAll()).willReturn(rooms);
        given(roomMapper.toRoomDTOList(rooms)).willReturn(List.of(roomDTO));

        List<RoomDTO> roomDTOList = roomService.getAllRoomsDTO();

        assertThat(roomDTOList).hasSize(1);
        assertThat(roomDTOList.get(0).getRoomNumber()).isEqualTo("101");
    }

    @Test
    void shouldFetchAllRoomsWithPagination() {
        RoomSearchCriteria roomSearchCriteria = new RoomSearchCriteria(null, null);
        roomSearchCriteria.setType(RoomType.SINGLE);
        roomSearchCriteria.setMinPrice(100.00);
        roomSearchCriteria.setMaxPrice(300.00);
        roomSearchCriteria.setAvailable(true);

        Pageable pageable = PageRequest.of(0, 5);
        Room room2 = new Room();
        room2.setRoomNumber("102");
        room2.setType(RoomType.SINGLE);
        room2.setAvailable(true);
        room2.setPrice(400.0);
        room2.setDescription("A comfortable room.");

        RoomDTO roomDTO2 = new RoomDTO();
        roomDTO2.setRoomNumber("102");
        roomDTO2.setType(RoomType.SINGLE);
        roomDTO2.setAvailable(true);
        roomDTO2.setPrice(400.0);
        roomDTO2.setDescription("A comfortable room.");

        List<Room> rooms = List.of(room);
        List<RoomDTO> roomDTOList = List.of(roomDTO);

        given(roomCriteriaRepository.getAllRooms(roomSearchCriteria, pageable)).willReturn(rooms);
        given(roomMapper.toRoomDTOList(rooms)).willReturn(roomDTOList);

        List<RoomDTO> result = roomService.getAllRooms(roomSearchCriteria, pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("101", result.get(0).getRoomNumber());

        verify(roomCriteriaRepository).getAllRooms(roomSearchCriteria, pageable);
    }


    @Test
    void shouldCreateMultipleRooms() {
        int numberOfRooms = 5;
        String startRoomNumber = "101";
        double price = 150.00;
        RoomType roomType = RoomType.SINGLE;

        List<RoomDTO> createdRoomDTOs = new ArrayList<>();
        List<Room> createdRooms = new ArrayList<>();
        given(roomRepository.existsByRoomNumber("101")).willReturn(true);

        for (int i = 0; i < numberOfRooms; i++) {
            String roomNumber = String.valueOf(Integer.parseInt(startRoomNumber) + i);

            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomNumber(roomNumber);
            roomDTO.setAvailable(true);
            roomDTO.setType(roomType);
            roomDTO.setPrice(price);
            createdRoomDTOs.add(roomDTO);

            Room room = new Room();
            room.setRoomNumber(roomNumber);
            room.setAvailable(true);
            room.setType(roomType);
            room.setPrice(price);
            createdRooms.add(room);

            given(roomRepository.existsByRoomNumber(roomNumber)).willReturn(false);
            given(roomMapper.toRoom(roomDTO)).willReturn(room);
            given(roomRepository.save(room)).willReturn(room);
            given(roomMapper.toRoomDto(room)).willReturn(roomDTO);
        }

        List<RoomDTO> result = roomService.createMultipleRooms(startRoomNumber, numberOfRooms, price, roomType);

        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    void shouldDeleteAllRooms() {
        given(roomRepository.findAll()).willReturn(List.of(room));

        roomService.deleteAllRooms();

        verify(roomRepository).delete(room);
    }

    @Test
    void shouldThrowExceptionWhenRoomHasActiveReservationsAndCannotBeDeleted() {
        Reservation reservation = new Reservation();
        reservation.setRoomId(room);
        reservation.setCheckInDate(LocalDate.of(2026, 12, 1));
        reservation.setCheckOutDate(LocalDate.of(2026, 12, 7));

        given(roomRepository.findById(1L)).willReturn(Optional.of(room));
        given(reservationRepository.findByRoomId(room)).willReturn(List.of(reservation));

        Throwable thrown = catchThrowable(() -> roomService.deleteRoom(1L));

        assertThat(thrown).isInstanceOf(RoomDeletionException.class)
                .hasMessage("Room with ID: 1 has active reservations and cannot be deleted.");

    }


}
