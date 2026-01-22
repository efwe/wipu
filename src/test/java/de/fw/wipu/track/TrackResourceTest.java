package de.fw.wipu.track;

import de.fw.wipu.track.internal.TrackService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class TrackResourceTest {

    @ConfigProperty(name = "wipu.basic-auth.user")
    String testUsername;

    @ConfigProperty(name = "wipu.basic-auth.password")
    String testPassword;

    @Inject
    TrackService trackService;

    @BeforeEach
    public void setup() {
        trackService.deleteAll().await().indefinitely();
    }

    @Test
    void createTrack_shouldParseGpxAndReturnSavedTrack() {
        URL gpxResource = getClass().getClassLoader().getResource("polar_00.gpx");
        Assertions.assertNotNull(gpxResource);
        File gpxFile = new File(gpxResource.getFile());

        String metadata = """
                {"title":"Morning ride","description":"Nice route"}
                """;

        given()
                .auth().basic(testUsername, testPassword)
                .multiPart("metadata", metadata, "application/json")
                .multiPart("gpx", gpxFile, "application/gpx+xml")
                .when()
                .post("/tracks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("title", equalTo("Morning ride"))
                .body("description", equalTo("Nice route"))
                .body("trackPoints", not(empty()))
                .body("trackPoints[0].id", nullValue()) // for now we don't need the PK of the point
                .body("trackPoints[0].trackId", nullValue()) // and we rather never need the track-id in the result
                .body("trackPoints[0].location", notNullValue())
                .body("trackPoints[0].time", notNullValue())
                .body("trackPoints[0].elevation", notNullValue())
                .body("distance", notNullValue())
                .body("startTime", notNullValue())
                .body("boundingBox", notNullValue());
    }

    @Test
    void getTracks_shouldReturnAllTracksWithoutPoints() {
        URL gpxResource = getClass().getClassLoader().getResource("polar_00.gpx");
        Assertions.assertNotNull(gpxResource);
        File gpxFile = new File(gpxResource.getFile());

        for (int i = 0; i < 3; i++) {
            String metadata = """
                {"title":"Track %d","description":"Description %d"}
                """.formatted(i, i);

            given()
                    .auth().basic(testUsername, testPassword)
                    .multiPart("metadata", metadata, "application/json")
                    .multiPart("gpx", gpxFile, "application/gpx+xml")
                    .when()
                    .post("/tracks")
                    .then()
                    .statusCode(200);
        }

        given()
                .auth().basic(testUsername, testPassword)
                .when()
                .get("/tracks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("title", containsInAnyOrder("Track 0", "Track 1", "Track 2"))
                .body("trackPoints", everyItem(nullValue()));
    }

    @Test
    void createAndGetATrack() throws java.io.IOException {
        URL gpxResource = getClass().getClassLoader().getResource("polar_00.gpx");
        Assertions.assertNotNull(gpxResource);
        File gpxFile = new File(gpxResource.getFile());

        String metadata = """
                {"title":"Morning ride","description":"Nice route"}
                """;

        Object trackId = given()
                .auth().basic(testUsername, testPassword)
                .multiPart("metadata", metadata, "application/json")
                .multiPart("gpx", gpxFile, "application/gpx+xml")
                .when()
                .post("/tracks")
                .then()
                .statusCode(200).extract().path("id");

        URL expectedJsonResource = getClass().getClassLoader().getResource("get_track_00.json");
        Assertions.assertNotNull(expectedJsonResource);
        File expectedJsonFile = new File(expectedJsonResource.getFile());
        String expectedJson = Files.readString(expectedJsonFile.toPath());

        String actualJson = given()
                .auth().basic(testUsername, testPassword)
                .when()
                .get("/tracks/{id}", trackId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedNode = mapper.readTree(expectedJson);
        ((com.fasterxml.jackson.databind.node.ObjectNode) expectedNode).put("id", trackId.toString());

        JsonNode actualNode = mapper.readTree(actualJson);
        Assertions.assertEquals(expectedNode, actualNode);
    }

}
