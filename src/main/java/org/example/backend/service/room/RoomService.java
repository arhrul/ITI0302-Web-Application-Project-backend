package org.example.backend.service.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.criteria.RoomSearchCriteria;
import org.example.backend.dtos.AmenityDTO;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.enums.RoomType;
import org.example.backend.exception.exceptions.NoSuchRoomException;
import org.example.backend.exception.exceptions.RoomDeletionException;
import org.example.backend.exception.exceptions.RoomNumberAlreadyExistsException;

import org.example.backend.mappers.RoomMapper;
import org.example.backend.model.Reservation;
import org.example.backend.model.Room;
import org.example.backend.repository.reservation.ReservationRepository;
import org.example.backend.repository.room.RoomCriteriaRepository;
import org.example.backend.repository.room.RoomRepository;

import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomCriteriaRepository roomCriteriaRepository;
    private final RoomMapper roomMapper;

    private final ReservationRepository reservationRepository;

    public RoomDTO createRoom(RoomDTO roomDTO) {
        log.info("Creating room with number: {}", roomDTO.getRoomNumber());
        validateRoomNumber(roomDTO.getRoomNumber(), null);
        Room room = roomMapper.toRoom(roomDTO);
        Room savedRoom = roomRepository.save(room);
        log.info("Room created successfully with ID: {}", savedRoom.getId());
        return roomMapper.toRoomDto(savedRoom);
    }

    public Room getRoomById(Long id) {
        log.info("Fetching room with ID: {}", id);
        return roomRepository.findById(id)
                .orElseThrow(() -> new NoSuchRoomException("Room not found!"));
    }

    private void validateRoomNumber(String roomNumber, Long roomId) {
        log.info("Validating room number: {}", roomNumber);
        if (roomRepository.existsByRoomNumber(roomNumber) && (roomId == null || !roomRepository.findById(roomId).get().getRoomNumber().equals(roomNumber))) {
                throw new RoomNumberAlreadyExistsException("Room with number " + roomNumber + " already exists.");
            }

    }

    public RoomDTO getRoom(Long id) {
        log.info("Fetching room: {}", id);
        Room room = getRoomById(id);
        return roomMapper.toRoomDto(room);
    }

    public List<Room> getAllRoomsEntity() {
        log.info("Fetching all rooms.");
        return roomRepository.findAll();
    }

    public List<RoomDTO> getAllRoomsDTO() {
        log.info("Fetching all rooms in DTO format.");
        return roomMapper.toRoomDTOList(getAllRoomsEntity());
    }

    public List<RoomDTO> getAllRooms(RoomSearchCriteria roomSearchCriteria, Pageable pageable) {
        log.info("Fetching rooms with pagination: {}", pageable);
        return roomMapper.toRoomDTOList(roomCriteriaRepository.getAllRooms(roomSearchCriteria, pageable));
    }

    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        log.info("Updating room with ID: {}", id);
        Room room = getRoomById(id);
        validateRoomNumber(roomDTO.getRoomNumber(), id);

        room.setRoomNumber(roomDTO.getRoomNumber());
        room.setPrice(roomDTO.getPrice());
        room.setAvailable(roomDTO.isAvailable());
        room.setType(roomDTO.getType());
        room.setDescription(roomDTO.getDescription());
        Room updatedRoom = roomRepository.save(room);
        log.info("Room with ID: {} updated successfully.", updatedRoom.getId());
        return roomMapper.toRoomDto(updatedRoom);
    }

    public void deleteRoom(Long id) {
        log.info("Attempting to delete room with ID: {}", id);
        Room room = getRoomById(id);

        if (hasReservations(room)) {
            log.warn("Room with ID: {} has active reservations. Cannot delete.", id);
            throw new RoomDeletionException("Room with ID: " + id + " has active reservations and cannot be deleted.");
        }

        log.info("Clearing all amenities associated with room with ID: {}", id);
        if (room.getAmenities() != null) {
            room.getAmenities().clear();
        } else {
            log.warn("Room with ID: {} has no amenities to clear.", id);
        }

        roomRepository.save(room);

        roomRepository.delete(room);
        log.info("Room with ID: {} deleted successfully.", id);
    }

    public List<RoomDTO> createMultipleRooms(String startRoomNumber, int numberOfRooms, double price, RoomType roomType) {
        log.info("Creating {} rooms starting from room number: {}", numberOfRooms, startRoomNumber);

        int startNumber = Integer.parseInt(startRoomNumber);
        List<RoomDTO> createdRooms = new ArrayList<>();

        for (int i = 0; i < numberOfRooms; i++) {
            String currentRoomNumber = String.valueOf(startNumber + i);

            if (roomRepository.existsByRoomNumber(currentRoomNumber)) {
                log.info("Room number {} already exists. Skipping.", currentRoomNumber);
                continue;
            }

            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomNumber(currentRoomNumber);
            roomDTO.setAvailable(true);
            roomDTO.setType(roomType);
            roomDTO.setPrice(price);

            RoomDTO createdRoom = createRoom(roomDTO);
            log.info(createdRoom.toString());
            createdRooms.add(createdRoom);
        }

        log.info("Created {} rooms.", createdRooms.size());
        return createdRooms;
    }

    public void deleteAllRooms() {
        log.info("Attempting to delete all rooms.");

        List<Room> rooms = getAllRoomsEntity();
        for (Room room : rooms) {
            try {
                log.info("Attempting to delete room with ID: {}", room.getId());
                if (hasReservations(room)) {
                    log.warn("Room with ID: {} has active reservations. Skipping deletion.", room.getId());
                    continue;
                }
                roomRepository.delete(room);
                log.info("Room with ID: {} deleted successfully.", room.getId());
            } catch (Exception e) {
                log.error("Failed to delete room with ID: {}. Reason: {}", room.getId(), e.getMessage());
            }
        }
        log.info("Completed deletion attempt for all rooms.");
    }

    private boolean hasReservations(Room room) {
        log.info("Checking for active reservations for room ID: {}", room.getId());
        List<Reservation> reservations = reservationRepository.findByRoomId(room);
        return !reservations.isEmpty();
    }

}
