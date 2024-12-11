package org.example.backend.controller.api;

import io.restassured.response.Response;
import static io.restassured.http.ContentType.JSON;

import org.example.backend.controller.BaseApi;
import org.example.backend.dtos.RoomDTO;

import static io.restassured.RestAssured.given;

public class RoomAmenityApi extends BaseApi {
    public static final String API_URL = BASE_API_URL + "/rooms";

    public Response getRooms() {
        return given()
                .when()
                .get(API_URL)
                .then()
                .extract().response();
    }

    public Response createRoom(RoomDTO room) {
        return given()
                .contentType(JSON.toString())
                .accept(JSON.toString())
                .body(room)
                .log().body()
                .when()
                .post(API_URL + "/private")
                .then()
                .log().body()
                .extract().response();
    }

    public Response getRoom(Long roomId) {
        return given()
                .when()
                .get(API_URL + "/" + roomId)
                .then()
                .log()
                .body()
                .extract().response();
    }

    public Response deleteRoom(Long roomId) {
        String token = AuthApi.getToken();
        return given()
                .header("Authorization", "Bearer ", token)
                .when()
                .delete(API_URL + "/private/" + roomId)
                .then()
                .extract().response();
    }

    public Response updateRoom(Long roomId, RoomDTO room) {
        String token = AuthApi.getToken();
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(JSON.toString())
                .accept(JSON.toString())
                .body(room)
                .log().body()
                .when()
                .put(API_URL + "/private/" + roomId)
                .then()
                .log().body()
                .extract().response();
    }
}
