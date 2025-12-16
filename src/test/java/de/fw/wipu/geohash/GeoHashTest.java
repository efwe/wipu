package de.fw.wipu.geohash;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GeoHashTest {


    @Test
    public void testValidInputs(){
        given()
                .when().get("/geohash/50/20/2025-12-01")
                .then()
                .statusCode(200)
                .body("location[0]", is(50.0f))
                .body("location[1]", is(20.0f))
                .body("date", is("2025-12-01"));
    }

}
