package de.fw.wipu.geohash.internal;

import de.fw.wipu.Location;
import de.fw.wipu.geohash.Forecast;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;


public class GeoHashWorkerTest {

    private static final Location SCHWABACH = new Location(11.020650, 49.329109);
    private static final Location PAPPENHEIM = new Location(10.97461, 48.93548);

    // As soon as our worker gets more dependencies we will switch to @QuarkusTest
    GeoHashWorker geoHashWorker = new GeoHashWorker();

    @Test
    public void testForecast20260114At1430NoResult() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 14, 12, 30)
                .atZone(ZoneId.of("Europe/Paris"))
                .toLocalDateTime();
        List<Forecast> result = geoHashWorker.forecast(dateTime);
        assertThat(result.size(), is(0));
    }

    @Test
    public void testForecast20260114At1600OneResult() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 14, 16, 0);
        List<Forecast> result = geoHashWorker.forecast(dateTime);
        assertThat(result.size(), is(1));
        assertThat(result.getFirst().latFraction(),is(closeTo(0.89864,0.0001)));
        assertThat(result.getFirst().lonFraction(),is(closeTo(0.96908,0.0001)));
    }

    @Test
    public void testForecast20260116At1730ThreeResults() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 16, 17, 30);
        List<Forecast> result = geoHashWorker.forecast(dateTime);
        assertThat(result.size(), is(3));
    }


    @Test
    public void testDjiaFor20251217() {
        String djia = geoHashWorker.djiaFor(20, LocalDate.of(2025, 12, 17));
        assertThat(djia, is("48380.17"));
    }

    @Test
    public void Checiny20251217() {
        Location location = geoHashWorker.locationFor(50, 20, "2025-12-17-48380.17");
        assertThat(location.getLon(), is(closeTo(20.40782, 0.0001)));
        assertThat(location.getLat(), is(closeTo(50.81788, 0.0001)));

    }

    @Test
    public void Schwabach20260111(){
        Location location = geoHashWorker.locationFor(49, 11, "2026-01-11-49234.81");
        assertThat(location.getLon(), is(closeTo(11.016798, 0.0001)));
        assertThat(location.getLat(), is(closeTo(49.396008, 0.0001)));
    }

    @Test
    public void afftectedGraticulesForSchwabachSameGraticule() {
        Set<Location> result = geoHashWorker.hashPointsWithinReach(SCHWABACH, new Location(11.0674,49.4561), 25);
        assertThat(result.size(), is(1));
        Location hashPoint = result.iterator().next();
        assertThat(hashPoint.getGraticule().getLat(),is(49.0));
        assertThat(hashPoint.getGraticule().getLon(),is(11.0));
    }

    @Test
    public void afftectedGraticulesForSchwabachWestGraticule() {
        Set<Location> result = geoHashWorker.hashPointsWithinReach(SCHWABACH, new Location(10.88642,49.34131), 25);
        assertThat(result.size(), is(1));
        Location hashPoint = result.iterator().next();
        assertThat(hashPoint.getGraticule().getLat(),is(49.0));
        assertThat(hashPoint.getGraticule().getLon(),is(10.0));
    }

    @Test
    public void affectedGraticulesForConfluenceMultiple() {
        // Center exactly at the confluence: corner of the 49/11 graticule
        Location center = new Location(11.0, 49.0);

        // Hashpoint roughly in the center of the same graticule
        Location geoHash = new Location(11.5, 49.5);

        // Large enough radius to catch all 3x3 neighboring graticules
        Set<Location> result = geoHashWorker.hashPointsWithinReach(center, geoHash, 200);

        // We expect all 9 hashpoints from the 3x3 neighborhood
        assertThat(result.size(), is(9));

        // Sanity-check that a corner graticule like (50, 12) is included
        boolean hasNorthEast =
                result.stream().anyMatch(l -> l.getGraticule().getLat() == 50.0
                        && l.getGraticule().getLon() == 12.0);
        assertThat(hasNorthEast, is(true));
    }

}
