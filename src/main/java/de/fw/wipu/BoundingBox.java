package de.fw.wipu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BoundingBox {
    private Location southWest;
    private Location northEast;

    public BoundingBox() {
    }

    public BoundingBox(Location southWest, Location northEast) {
        this.southWest = southWest;
        this.northEast = northEast;
    }

    public static BoundingBox fromString(String bbox) {
        if (bbox == null || bbox.isBlank()) {
            return null;
        }
        List<Double> points = Arrays.stream(bbox.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .toList();

        if (points.size() != 4) {
            throw new IllegalArgumentException("Bounding box must contain exactly 4 coordinates (minLon, minLat, maxLon, maxLat)");
        }

        Location sw = new Location(points.get(0), points.get(1));
        Location ne = new Location(points.get(2), points.get(3));
        return new BoundingBox(sw, ne);
    }

    public Location getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Location southWest) {
        this.southWest = southWest;
    }

    public Location getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Location northEast) {
        this.northEast = northEast;
    }
}
