package de.fw.wipu.track.internal;

import de.fw.wipu.BoundingBox;
import de.fw.wipu.Location;
import de.fw.wipu.track.Track;
import de.fw.wipu.track.TrackPoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TrackServiceTest {

    @Inject
    TrackService trackService;

    @BeforeEach
    public void setup() {
        trackService.deleteAll().await().indefinitely();
    }

    @Test
    public void testSaveAndFindTrack() {
        Track track = new Track();
        track.setStartTime(LocalDateTime.of(2026, 1, 21, 10, 0));
        track.setEndTime(LocalDateTime.of(2026, 1, 21, 11, 0));

        Location sw = new Location(10.0, 48.0);
        Location ne = new Location(11.0, 49.0);
        track.setBoundingBox(new BoundingBox(sw, ne));

        List<TrackPoint> points = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            points.add(new TrackPoint(new Location(10.0 + i * 0.1, 48.0 + i * 0.1),
                    track.getStartTime().plusMinutes(i * 10), 100.0 + i));
        }
        track.setTrackPoints(points);

        // Save
        Track savedTrack = trackService.save(track).await().indefinitely();
        assertThat(savedTrack.getId(), is(notNullValue()));
        assertThat(savedTrack.getTrackPoints().get(0).getTrackId(), is(savedTrack.getId()));
        assertThat(savedTrack.getTrackPoints().get(0).getId(), is(notNullValue()));

        // Retrieve
        Track retrievedTrack = trackService.findTrack(savedTrack.getId()).await().indefinitely();
        assertThat(retrievedTrack, is(notNullValue()));
        assertThat(retrievedTrack.getId(), is(savedTrack.getId()));
        assertThat(retrievedTrack.getStartTime(), is(track.getStartTime()));
        assertThat(retrievedTrack.getBoundingBox().getSouthWest(), is(sw));
        assertThat(retrievedTrack.getBoundingBox().getNorthEast(), is(ne));

        assertThat(retrievedTrack.getTrackPoints(), hasSize(5));
        assertThat(retrievedTrack.getTrackPoints().get(0).getLocation().getLon(), is(10.0));
        assertThat(retrievedTrack.getTrackPoints().get(4).getElevation(), is(104.0));
    }

}
