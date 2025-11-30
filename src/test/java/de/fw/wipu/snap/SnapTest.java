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


}
