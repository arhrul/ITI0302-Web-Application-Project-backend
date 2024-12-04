package org.example.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.RoomType;

@Data
@NoArgsConstructor
public class MultipleRoomsDTO {

    private String startRoomNumber;
    private int numberOfRooms;
    private double price;
    private RoomType type;
}
