package de.fw.wipu.geohash.internal;

import de.fw.wipu.Location;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;


@QuarkusTest
public class GeoHashWorkerTest {
    @Inject
    GeoHashWorker geoHashWorker;

    @Test
    public void testDjiaFor20251217(){
        String djia = geoHashWorker.djiaFor(20, LocalDate.of(2025, 12, 17));
        assertThat(djia, is("48380.17"));
    }

    @Test
    public void Checiny20251217(){
        Location location = geoHashWorker.locationFor(50, 20, "2025-12-17-48380.17");
        assertThat(location.getCoordinates().get(0), is(closeTo(20.40782, 0.0001)));
        assertThat(location.getCoordinates().get(1), is(closeTo(50.81788, 0.0001)));

    }
}
