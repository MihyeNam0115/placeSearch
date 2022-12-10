package com.mh.placesearch.controller;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlaceSearchControllerTest {

    @LocalServerPort
    private int serverPort;

    @BeforeAll
    void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        System.out.println(RestAssured.baseURI);
        System.out.println(RestAssured.port);
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    void searchByKeyword_whenNormal_shouldReturn200() {
        RestAssured.given().queryParam("q", "abc").when().get("/v1/place").then().statusCode(200);
    }

    @Test
    void searchByKeyword_whenNoQueryParam_shouldReturn400() {
        RestAssured.given().when().get("/v1/place").then().statusCode(400);
    }

    @Test
    void searchByKeyword_whenWrongUrl_shouldReturn404() {
        RestAssured.given().when().get("/v1/placeWrong").then().statusCode(404);
    }
}
