package com.mh.placesearch.controller;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        RestAssured.given()
                .queryParam("q", "abc")
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);
    }

    @Test
    void searchByKeyword_whenNormalGubchang_shouldReturn200() {
        RestAssured.given()
                .queryParam("q", "곱창")
                .when().get("/v1/place")
                .then().assertThat().statusCode(200)
                .body("places", notNullValue())
                .body("places[0].title", notNullValue())
                .body("places.size()", equalTo(9));
    }

    @Test
    void searchByKeyword_whenNoQueryParam_shouldReturn400() {
        RestAssured.given()
                .when().get("/v1/place")
                .then().assertThat().statusCode(400);
    }

    @Test
    void searchByKeyword_whenWrongUrl_shouldReturn404() {
        RestAssured.given()
                .when().get("/v1/placeWrong")
                .then().assertThat().statusCode(404);
    }

    @Test
    void keywordRanking_whenNormal_shouldReturn200() {
        RestAssured.given()
                .when().get("/v1/keywordRanking")
                .then().assertThat().statusCode(200)
                .body("keywords", notNullValue());
    }

    @Test
    @Order(1)
    void keywordRanking_whenAfterSearch_shouldReturn200WithKeywords() {
        String newKeyword = RandomStringUtils.randomAlphanumeric(20);
        RestAssured.given()
                .queryParam("q", newKeyword)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .when().get("/v1/keywordRanking")
                .then().assertThat().statusCode(200)
                .body("keywords", notNullValue())
                .body("keywords[0].keyword", equalTo(newKeyword))
                .body("keywords[0].count", equalTo(1))
        ;
    }

    @Test
    @Order(2)
    void keywordRanking_whenAfterMultipleSearch_shouldReturn200WithKeywords() {
        String newKeyword1 = RandomStringUtils.randomAlphanumeric(20);
        String newKeyword2 = RandomStringUtils.randomAlphanumeric(20);

        RestAssured.given()
                .queryParam("q", newKeyword1)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .queryParam("q", newKeyword2)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .queryParam("q", newKeyword1)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .queryParam("q", newKeyword2)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .queryParam("q", newKeyword1)
                .when().get("/v1/place")
                .then().assertThat().statusCode(200);

        RestAssured.given()
                .when().get("/v1/keywordRanking")
                .then().assertThat().statusCode(200)
                .body("keywords", notNullValue())
                .body("keywords[0].keyword", equalTo(newKeyword1))
                .body("keywords[0].count", equalTo(3))
                .body("keywords[1].keyword", equalTo(newKeyword2))
                .body("keywords[1].count", equalTo(2))
        ;
    }
}
