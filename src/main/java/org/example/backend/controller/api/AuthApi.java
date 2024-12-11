package org.example.backend.controller.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.backend.controller.BaseApi;
import org.example.backend.dtos.auth.LoginRequestDTO;
import org.example.backend.dtos.auth.LoginResponseDTO;

import static io.restassured.RestAssured.given;

public class AuthApi extends BaseApi {
    public static final String API_URL = BASE_API_URL + "/login";
    private static final String EMAIL = "admin2@example.com";
    private static final String PASSWORD = "12345678";

    public static String getToken() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        //LoginResponseDTO loginResponseDTO = new LoginResponseDTO();

        Response authResponse = given()
                .header("Content-Type", ContentType.JSON.toString())
                .body(request)
                .log()
                .headers()
                .and().log()
                .body()
                .when()
                .post(API_URL + "/login")
                .then()
                .log()
                .body()
                .and()
                .extract().response();

        //return loginResponseDTO.getToken();
        return authResponse.getBody().jsonPath().getString("token");
        }
}
