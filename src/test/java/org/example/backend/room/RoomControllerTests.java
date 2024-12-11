package org.example.backend.room;

import io.restassured.response.Response;
import org.example.backend.controller.api.RoomAmenityApi;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.dtos.response.RoomResponse;
import org.example.backend.generator.RoomGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.withPrecision;

public class RoomControllerTests {
    private static RoomAmenityApi roomAmenityApi;
    private static final RoomDTO room = RoomGenerator.generateRoom();

    @BeforeAll
    public static void instantiateRestAssured() {
        roomAmenityApi = new RoomAmenityApi();
    }

    @Test
    public void whenGetRoomsIsCalled_thenReturnHttp200() {
        Response getRoomsResponse = roomAmenityApi.getRooms();

        assertThat(getRoomsResponse.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void whenCreateRoomIsCalled_thenReturnHttp200() {
        Response createRoomResponse = roomAmenityApi.createRoom(room);

        assertThat(createRoomResponse.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void whenGetRoomByIdIsCalled_thenReturnHttp200() {
        RoomResponse createdRoomResponse = roomAmenityApi
                .createRoom(room)
                .as(RoomResponse.class);

        Response getRoomByIdResponse = roomAmenityApi.getRoom(createdRoomResponse.getRoom().getId());

        assertThat(getRoomByIdResponse.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void whenUpdateRoomIsCalled_thenReturnHttp200() {
        RoomResponse roomResponse = roomAmenityApi
                .createRoom(room)
                .as(RoomResponse.class);
        Long roomId = roomResponse.getRoom().getId();

        RoomDTO updatedRoom = new RoomDTO();
        updatedRoom.setRoomNumber("102");
        updatedRoom.setPrice(200.0);

        Response updateRoomResponse = roomAmenityApi.updateRoom(roomId, updatedRoom);

        assertThat(updateRoomResponse.getStatusCode()).isEqualTo(200);
        assertThat(updateRoomResponse.jsonPath().getString("roomNumber")).isEqualTo("102");
        assertThat(updateRoomResponse.jsonPath().getDouble("price")).isEqualTo(200.0, withPrecision(0.01));
    }

    @Test
    public void whenDeleteRoomIsCalled_thenReturnHttp200() {
        RoomResponse roomResponse = roomAmenityApi
                .createRoom(room)
                .as(RoomResponse.class);

        Long roomId = roomResponse.getRoom().getId();
        Response deleteRoomResponse = roomAmenityApi.deleteRoom(roomId);

        assertThat(deleteRoomResponse.getStatusCode()).isEqualTo(204);
    }
}
