package de.fw.wipu.track.internal;

import de.fw.wipu.BoundingBox;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GpxParserTest {

    @Test
    void testParsePolar00() throws Exception {
        
        GpxParser parser = new GpxParser();
        GPX gpx;
        try (InputStream is = getClass().getResourceAsStream("/polar_00.gpx")) {
            assertThat("polar_00.gpx not found in resources", is, notNullValue());
            gpx = parser.parse(is);
        }

        assertThat(gpx, notNullValue());

        // Bounding box assertion
        // <bounds minlat="49.310445" minlon="11.019095" maxlat="49.31916" maxlon="11.03291833"/>
        BoundingBox bbox = parser.getBoundingBox(gpx);
        assertThat(bbox, notNullValue());
        assertThat(bbox.getSouthWest().getLon(), closeTo(11.019095, 0.000001));
        assertThat(bbox.getSouthWest().getLat(), closeTo(49.310445, 0.000001));
        assertThat(bbox.getNorthEast().getLon(), closeTo(11.03291833, 0.000001));
        assertThat(bbox.getNorthEast().getLat(), closeTo(49.31916, 0.000001));

        // Start time assertion
        // First trkpt: <time>2026-01-16T15:37:23.735Z</time>
        Instant startTime = parser.getStartTime(gpx);
        assertThat(startTime, notNullValue());
        assertThat(startTime, equalTo(ZonedDateTime.parse("2026-01-16T15:37:23.735Z").toInstant()));

        // End time assertion
        // Last trkpt: <time>2026-01-16T16:34:54.735Z</time>
        Instant endTime = parser.getEndTime(gpx);
        assertThat(endTime, notNullValue());
        assertThat(endTime, equalTo(ZonedDateTime.parse("2026-01-16T16:34:54.735Z").toInstant()));

        // Point count assertion
        long pointCount = parser.getPointCount(gpx);
        assertThat(pointCount, equalTo(3400L));

        // Distance assertion
        Length distance = parser.getDistance(gpx);
        // Note: Polar homepage was more pessimistic with 3.8km gpx.studio says 4.1km - so altogether 4013m is good enough:)
        assertThat(distance.doubleValue(), closeTo(4013, 10.0));
    }

    @Test
    void testParseGarmin00() throws Exception {

        GpxParser parser = new GpxParser();
        GPX gpx;
        try (InputStream is = getClass().getResourceAsStream("/garmin_00.gpx")) {
            assertThat("garmin_00.gpx not found in resources", is, notNullValue());
            gpx = parser.parse(is);
        }

        assertThat(gpx, notNullValue());

        // Distance assertion
        Length distance = parser.getDistance(gpx);
        // Note: Polar homepage was more pessimistic with 3.8km gpx.studio says 4.1km - so altogether 4013m is good enough:)
        assertThat(distance.doubleValue(), closeTo(55989, 10.0));

        BoundingBox bbox = parser.getBoundingBox(gpx);
        assertThat(bbox, notNullValue());
        assertThat(bbox.getSouthWest().getLon(), closeTo(20.459396, 0.000001));
        assertThat(bbox.getSouthWest().getLat(), closeTo(50.799815, 0.000001));
        assertThat(bbox.getNorthEast().getLon(), closeTo(20.531908, 0.000001));
        assertThat(bbox.getNorthEast().getLat(), closeTo(50.968978, 0.000001));

    }

}
