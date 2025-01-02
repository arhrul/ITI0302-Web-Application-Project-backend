package org.example.backend.service.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.AmenityDTO;
import org.example.backend.exception.exceptions.AmenityAlreadyAssignedException;
import org.example.backend.exception.exceptions.NoSuchAmenityException;
import org.example.backend.exception.exceptions.NoSuchRoomException;
import org.example.backend.mappers.AmenityMapper;
import org.example.backend.model.Amenity;
import org.example.backend.model.Room;
import org.example.backend.repository.room.AmenityRepository;
import org.example.backend.repository.room.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomAmenityService {

    private final RoomRepository roomRepository;
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;
    public static final String ROOM_NOT_FOUND_MESSAGE = "Room not found!";
    private static final String AMENITY_NOT_FOUND = "Amenity not found!";

    public void assignAmenityToRoom(Long roomId, Long amenityId) {
        log.info("Attempting to assign amenity with ID: {} to room with ID: {}", amenityId, roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchRoomException(ROOM_NOT_FOUND_MESSAGE));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new NoSuchAmenityException(AMENITY_NOT_FOUND));

        if (room.getAmenities().contains(amenity)) {
            log.warn("Amenity with ID: {} is already assigned to room with ID: {}", amenityId, roomId);
            throw new AmenityAlreadyAssignedException("Amenity is already assigned to this room.");
        }

        room.getAmenities().add(amenity);
        double newPrice = room.getPrice() + amenity.getAdditionalCost();
        room.setPrice(newPrice);
        roomRepository.save(room);
        log.info("Amenity assigned successfully. Room price updated to {}", newPrice);
    }

    public List<AmenityDTO> getAmenitiesByRoom(Long roomId) {
        log.info("Fetching amenities for room with ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchRoomException(ROOM_NOT_FOUND_MESSAGE));

        List<Amenity> amenities = room.getAmenities();
        log.info("Found {} amenities for room with ID: {}", amenities.size(), roomId);
        return amenityMapper.toAmenityDTOList(amenities);
    }

    public void removeAmenityFromRoom(Long roomId, Long amenityId) {
        log.info("Attempting to remove amenity with ID: {} from room with ID: {}", amenityId, roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchRoomException(ROOM_NOT_FOUND_MESSAGE));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new NoSuchAmenityException(AMENITY_NOT_FOUND));

        if (!room.getAmenities().contains(amenity)) {
            log.warn("Amenity with ID: {} is not assigned to room with ID: {}", amenityId, roomId);
            throw new NoSuchAmenityException("Amenity is not assigned to this room.");
        }

        room.getAmenities().remove(amenity);
        double newPrice = room.getPrice() - amenity.getAdditionalCost();
        room.setPrice(newPrice);
        roomRepository.save(room);
        log.info("Amenity removed successfully. Room price updated to {}", newPrice);

    }
}

