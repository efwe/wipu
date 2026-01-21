package de.fw.wipu.track.internal;

import de.fw.wipu.BoundingBox;
import de.fw.wipu.track.Track;
import de.fw.wipu.track.TrackPoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TrackWorkerTest {

    @Inject
    TrackWorker trackWorker;


    @Test
    void testParseTrack() throws Exception {


        Track track;
        try (InputStream is = getClass().getResourceAsStream("/polar_00.gpx")) {
            assertThat("polar_00.gpx not found in resources", is, notNullValue());
            track = trackWorker.parseTrack(is);
        }

        assertThat(track, notNullValue());

        // BoundingBox
        BoundingBox bbox = track.getBoundingBox();
        assertThat(bbox, notNullValue());
        assertThat(bbox.getSouthWest().getLon(), closeTo(11.019095, 0.000001));
        assertThat(bbox.getSouthWest().getLat(), closeTo(49.310445, 0.000001));
        assertThat(bbox.getNorthEast().getLon(), closeTo(11.03291833, 0.000001));
        assertThat(bbox.getNorthEast().getLat(), closeTo(49.31916, 0.000001));

        // Times
        assertThat(track.getStartTime(), equalTo(LocalDateTime.of(2026, 1, 16, 15, 37, 23, 735000000)));
        assertThat(track.getEndTime(), equalTo(LocalDateTime.of(2026, 1, 16, 16, 34, 54, 735000000)));

        // Distance
        assertThat(track.getDistance(), closeTo(4013L, 10L));

        // TrackPoints
        List<TrackPoint> points = track.getTrackPoints();
        assertThat(points, notNullValue());
        assertThat(points.size(), equalTo(3400));

        // Verify first point has location and time
        TrackPoint firstPoint = points.getFirst();
        assertThat(firstPoint.getLocation(), notNullValue());
        assertThat(firstPoint.getTime(), notNullValue());
    }
}
