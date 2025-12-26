package de.fw.wipu.snap;

import de.fw.wipu.Location;
import com.mongodb.client.MongoClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SnapTest {


    @Inject
    MongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    @Test
    void snapsDontNeedAuth() {
        given()
                .when().get("/snaps")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @BeforeEach
    void clearDatabase() {
        mongoClient.getDatabase(databaseName)
                .getCollection(Snap.SNAP_COLLECTION_NAME)
                .drop();
    }

    @Test
    void savedSnapRendersLatLonInJson() {
        Snap snap = new Snap();
        snap.setLocation(new Location(13.404954, 52.520008));
        snap.setTitle("test.jpg");
        snap.setSecret("0x00");
        snap.setSnapId(123456789L);
        snap.setServer("65535");


        mongoClient.getDatabase(databaseName)
                .getCollection(Snap.SNAP_COLLECTION_NAME, Snap.class)
                .insertOne(snap);

        given()
                .when().get("/snaps")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].snapId", is(123456789))
                .body("[0].title", is("test.jpg"))
                .body("[0].secret", is("0x00"))
                .body("[0].server", is("65535"))
                .body("[0].location[0]", is(52.520008F))
                .body("[0].location[1]", is(13.404954F));
    }

    @Test
    void snapsCanBeFilteredByBoundingBox() {
        // Berlin
        Snap snap1 = new Snap();
        snap1.setLocation(new Location(13.404954, 52.520008));
        snap1.setTitle("Berlin.jpg");
        snap1.setSnapId(1L);

        // Munich
        Snap snap2 = new Snap();
        snap2.setLocation(new Location(11.581981, 48.135125));
        snap2.setTitle("Munich.jpg");
        snap2.setSnapId(2L);

        mongoClient.getDatabase(databaseName)
                .getCollection(Snap.SNAP_COLLECTION_NAME, Snap.class)
                .insertOne(snap1);
        mongoClient.getDatabase(databaseName)
                .getCollection(Snap.SNAP_COLLECTION_NAME, Snap.class)
                .insertOne(snap2);

        // Bounding box for Berlin area: approx 13.0, 52.4 to 13.8, 52.6
        given()
                .when().get("/snaps?bbox=13.0,52.4,13.8,52.6")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].title", is("Berlin.jpg"));

        // Bounding box for Germany
        given()
                .when().get("/snaps?bbox=5.0,47.0,15.0,55.0")
                .then()
                .statusCode(200)
                .body("size()", is(2));

        // Bounding box for nowhere
        given()
                .when().get("/snaps?bbox=0.0,0.0,1.0,1.0")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }


}
