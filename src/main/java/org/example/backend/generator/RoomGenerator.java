package org.example.backend.generator;

import net.datafaker.Faker;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.enums.RoomType;

import java.util.Random;

public class RoomGenerator {

    private static final Random RANDOM = new Random();
    private static final Faker FAKER = new Faker();

    public static RoomDTO generateRoom() {
        RoomDTO room = new RoomDTO();
        room.setRoomNumber(String.valueOf(FAKER.number().numberBetween(100, 1000)));
        room.setPrice(FAKER.number().randomDouble(2, 50, 200));
        room.setAvailable(true);
        RoomType[] roomTypes = RoomType.values();
        room.setType(roomTypes[RANDOM.nextInt(roomTypes.length)]);
        room.setDescription(FAKER.harryPotter().quote());

        return room;
    }
}
