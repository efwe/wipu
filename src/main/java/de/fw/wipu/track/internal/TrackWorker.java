package de.fw.wipu.track.internal;

import de.fw.wipu.Location;
import de.fw.wipu.track.Track;
import de.fw.wipu.track.TrackPoint;
import io.jenetics.jpx.GPX;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * The TrackWorker is implemented in internal and exposes the public API for track parsing and manipulation.
 * To be used in the Resource layer.
 * Motto: I do not leak jenetics.jpx instances but I can use them internally here
 */
@ApplicationScoped
public class TrackWorker {


    public Track parseTrack(InputStream inputStream) throws IOException {
        GpxParser parser = new GpxParser();
        GPX gpx = parser.parse(inputStream);

        de.fw.wipu.track.Track track = new de.fw.wipu.track.Track();
        track.setBoundingBox(parser.getBoundingBox(gpx));
        track.setStartTime(toLocalDateTime(parser.getStartTime(gpx)));
        track.setEndTime(toLocalDateTime(parser.getEndTime(gpx)));
        track.setDistance(parser.getDistance(gpx).doubleValue());
        track.setTrackPoints(getTrackPoints(gpx));

        return track;
    }


    List<TrackPoint> getTrackPoints(GPX gpx) {
        return gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .flatMap(io.jenetics.jpx.TrackSegment::points)
                .map(point -> new TrackPoint(
                        new Location(point.getLongitude().doubleValue(), point.getLatitude().doubleValue()),
                        point.getTime().map(t -> LocalDateTime.ofInstant(t, ZoneOffset.UTC)).orElse(null),
                        point.getElevation().map(e -> e.doubleValue()).orElse(0.0)
                ))
                .toList();
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


}
