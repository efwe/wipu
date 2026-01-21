package de.fw.wipu.track.internal;

import de.fw.wipu.BoundingBox;
import de.fw.wipu.Location;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.geom.Geoid;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;

class GpxParser {

    GPX parse(Path path) throws IOException {
        return GPX.read(path);
    }

    GPX parse(InputStream inputStream) throws IOException {

        return GPX.Reader.of(GPX.Version.V11, GPX.Reader.Mode.STRICT).read(inputStream);
    }

    BoundingBox getBoundingBox(GPX gpx) {
        return gpx.getMetadata()
                .flatMap(io.jenetics.jpx.Metadata::getBounds)
                .map(bounds -> new BoundingBox(
                        new Location(bounds.getMinLongitude().doubleValue(), bounds.getMinLatitude().doubleValue()),
                        new Location(bounds.getMaxLongitude().doubleValue(), bounds.getMaxLatitude().doubleValue())
                ))
                .orElseGet(() -> boundingBoxFor(gpx));
    }

    private BoundingBox boundingBoxFor(GPX gpx) {
        double[] acc = gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .flatMap(io.jenetics.jpx.TrackSegment::points)
                .collect(
                        () -> new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                        (a, point) -> {
                            double lat = point.getLatitude().doubleValue();
                            double lon = point.getLongitude().doubleValue();
                            if (lat < a[0]) a[0] = lat;
                            if (lon < a[1]) a[1] = lon;
                            if (lat > a[2]) a[2] = lat;
                            if (lon > a[3]) a[3] = lon;
                        },
                        (a1, a2) -> {
                            if (a2[0] < a1[0]) a1[0] = a2[0];
                            if (a2[1] < a1[1]) a1[1] = a2[1];
                            if (a2[2] > a1[2]) a1[2] = a2[2];
                            if (a2[3] > a1[3]) a1[3] = a2[3];
                        }
                );
        if (acc[0] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return new BoundingBox(
                new Location(acc[1], acc[0]),
                new Location(acc[3], acc[2])
        );
    }

    Instant getStartTime(GPX gpx) {
        return gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .flatMap(io.jenetics.jpx.TrackSegment::points)
                .flatMap(point -> point.getTime().stream())
                .min(java.time.Instant::compareTo)
                .orElseThrow(() -> new IllegalStateException("GPX file does not contain any time information"));
    }

    Instant getEndTime(GPX gpx) {
        return gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .flatMap(io.jenetics.jpx.TrackSegment::points)
                .flatMap(point -> point.getTime().stream())
                .max(java.time.Instant::compareTo)
                .orElseThrow(() -> new IllegalStateException("GPX file does not contain any time information"));
    }

    long getPointCount(GPX gpx) {
        return gpx.tracks()
                .flatMap(io.jenetics.jpx.Track::segments)
                .flatMap(io.jenetics.jpx.TrackSegment::points)
                .count();
    }

    Length getDistance(GPX gpx) {
        final Length length;
        return gpx.tracks()
                .flatMap(Track::segments)
                .flatMap(TrackSegment::points)
                .collect(Geoid.WGS84.toPathLength());
    }


}
