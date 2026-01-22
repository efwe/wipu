package de.fw.wipu.track;

import de.fw.wipu.BoundingBox;
import de.fw.wipu.Location;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TrackModelTest {

    @Test
    void testTrackModeling() {
        Location loc1 = new Location(11.0, 49.0);
        LocalDateTime time1 = LocalDateTime.of(2026, 1, 21, 10, 0);
        TrackPoint tp1 = new TrackPoint(loc1, time1, 100.0);

        Location loc2 = new Location(11.1, 49.1);
        LocalDateTime time2 = LocalDateTime.of(2026, 1, 21, 11, 0);
        TrackPoint tp2 = new TrackPoint(loc2, time2, 200.0);

        BoundingBox bbox = new BoundingBox(
                new Location(11.0, 49.0), // SW
                new Location(11.1, 49.1), // NE
                new Location(11.1, 49.0), // SE
                new Location(11.0, 49.1)  // NW
        );

        Track track = new Track();
        track.setStartTime(time1);
        track.setEndTime(time2);
        track.setBoundingBox(bbox);
        track.setTrackPoints(List.of(tp1, tp2));

        assertThat(track.getStartTime(), equalTo(time1));
        assertThat(track.getEndTime(), equalTo(time2));
        assertThat(track.getBoundingBox(), equalTo(bbox));
        assertThat(track.getTrackPoints(), hasSize(2));
        assertThat(track.getTrackPoints().get(0).getElevation(), equalTo(100.0));
    }

    @Test
    void testBoundingBoxAliases() {
        Location sw = new Location(11.0, 49.0);
        Location ne = new Location(11.1, 49.1);
        Location se = new Location(11.1, 49.0);
        Location nw = new Location(11.0, 49.1);

        BoundingBox bbox = new BoundingBox(sw, ne, se, nw);

        assertThat(bbox.SW(), equalTo(sw));
        assertThat(bbox.NE(), equalTo(ne));
        assertThat(bbox.SE(), equalTo(se));
        assertThat(bbox.NW(), equalTo(nw));
    }

    @Test
    void testBoundingBoxTwoArgConstructor() {
        Location sw = new Location(11.0, 49.0);
        Location ne = new Location(11.1, 49.1);

        BoundingBox bbox = new BoundingBox(sw, ne);

        assertThat(bbox.SW(), equalTo(sw));
        assertThat(bbox.NE(), equalTo(ne));
        assertThat(bbox.SE().getLon(), equalTo(11.1));
        assertThat(bbox.SE().getLat(), equalTo(49.0));
        assertThat(bbox.NW().getLon(), equalTo(11.0));
        assertThat(bbox.NW().getLat(), equalTo(49.1));
    }
}
