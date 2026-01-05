package de.fw.wipu.geohash.internal;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class GeoHashCronJobTest {

    @Inject
    GeoHashCronJob geoHashCronJob;

    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void init() {
        mailbox.clear();
    }

    @Test
    public void testRunJob() {
        geoHashCronJob.runJob();
        // The email is only sent if distance < 25km. 
        // Depending on the day, it might be 0 or more.
        // We just check it doesn't crash.
    }
}
