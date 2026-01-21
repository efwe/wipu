package de.fw.wipu;

import java.util.Arrays;
import java.util.List;

public class BoundingBox {
    private Location southEast;
    private Location northEast;
    private Location southWest;
    private Location northWest;

    public BoundingBox() {
    }

    public BoundingBox(Location southWest, Location northEast, Location southEast, Location northWest) {
        this.southWest = southWest;
        this.northEast = northEast;
        this.southEast = southEast;
        this.northWest = northWest;
    }

    public BoundingBox(Location southWest, Location northEast) {
        this(southWest, northEast, new Location(northEast.getLon(), southWest.getLat()), new Location(southWest.getLon(), northEast.getLat()));
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
        Location se = new Location(points.get(2), points.get(1));
        Location nw = new Location(points.get(0), points.get(3));
        return new BoundingBox(sw, ne, se, nw);
    }

    public Location getSouthEast() {
        return southEast;
    }

    public void setSouthEast(Location southEast) {
        this.southEast = southEast;
    }

    public Location getNorthEast() {
        return northEast;
    }

    public void setNorthEast(Location northEast) {
        this.northEast = northEast;
    }

    public Location getSouthWest() {
        return southWest;
    }

    public void setSouthWest(Location southWest) {
        this.southWest = southWest;
    }

    public Location getNorthWest() {
        return northWest;
    }

    public void setNorthWest(Location northWest) {
        this.northWest = northWest;
    }

    public Location getSE() {
        return southEast;
    }

    public Location getNE() {
        return northEast;
    }

    public Location getSW() {
        return southWest;
    }

    public Location getNW() {
        return northWest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return java.util.Objects.equals(southEast, that.southEast) && java.util.Objects.equals(northEast, that.northEast) && java.util.Objects.equals(southWest, that.southWest) && java.util.Objects.equals(northWest, that.northWest);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(southEast, northEast, southWest, northWest);
    }
}
