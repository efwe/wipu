package de.fw.wipu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Location {

    private static final double EARTH_RADIUS_METERS = 6_371_000d;

    @JsonIgnore
    @BsonProperty("coordinates")
    private List<Double> coordinates;

    // No-arg constructor required by MongoDB PojoCodec
    public Location() {
    }

    public Location(Double lon, Double lat) {
        this.coordinates = new ArrayList<>(List.of(lon, lat));
    }

    // Expose as [lat, lon] for JSON while storing as [lon, lat] in MongoDB
    @JsonProperty("location")
    @BsonIgnore
    public Double[] getJsonCoordinates() {
        if (coordinates == null || coordinates.size() < 2) {
            return null;
        }
        return new Double[]{coordinates.getLast(), coordinates.getFirst()};
    }

    @JsonIgnore
    public String getType() {
        return "Point";
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    /**
     * used by jackson - do not remove :)
     *
     * @param coordinates the coordinates to set
     */
    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    @JsonIgnore
    @BsonIgnore
    public Double getLon() {
        return coordinates.getFirst();
    }

    @JsonIgnore
    @BsonIgnore
    public Double getLat() {
        return coordinates.getLast();
    }

    @JsonIgnore
    @BsonIgnore
    public Location getGraticule() {
        if (this.coordinates == null || this.coordinates.size() < 2) {
            return new Location(0.0, 0.0);
        }
        return new Location(Math.floor(this.coordinates.getFirst()), Math.floor(this.coordinates.getLast()));
    }

    @JsonIgnore
    @BsonIgnore
    public Double getLatFraction() {
        if (this.coordinates == null || this.coordinates.size() < 2) {
            return 0.0;
        }
        return this.coordinates.getLast() - Math.floor(this.coordinates.getLast());
    }

    @JsonIgnore
    @BsonIgnore
    public Double getLonFraction() {
        if (this.coordinates == null || this.coordinates.size() < 2) {
            return 0.0;
        }
        return this.coordinates.getFirst() - Math.floor(this.coordinates.getFirst());
    }

    /**
     * Returns a new Location north of this one by the given distance in kilometers.
     */
    @JsonIgnore
    @BsonIgnore
    public Location north(int km) {
        return moveByKilometers(km, 0.0);
    }

    /**
     * Returns a new Location east of this one by the given distance in kilometers.
     */
    @JsonIgnore
    @BsonIgnore
    public Location east(int km) {
        return moveByKilometers(km, 90.0);
    }

    /**
     * Returns a new Location south of this one by the given distance in kilometers.
     */
    @JsonIgnore
    @BsonIgnore
    public Location south(int km) {
        return moveByKilometers(km, 180.0);
    }

    /**
     * Returns a new Location west of this one by the given distance in kilometers.
     */
    @JsonIgnore
    @BsonIgnore
    public Location west(int km) {
        return moveByKilometers(km, 270.0);
    }

    /**
     * Move this location along a great-circle by the given distance (km) and bearing (degrees, 0=north).
     */
    private Location moveByKilometers(double distanceKm, double bearingDeg) {
        if (coordinates == null || coordinates.size() < 2) {
            throw new IllegalStateException("Location has no coordinates");
        }

        double lon1 = Math.toRadians(getLon());
        double lat1 = Math.toRadians(getLat());
        double bearing = Math.toRadians(bearingDeg);

        double distanceMeters = distanceKm * 1000.0;
        double angularDistance = distanceMeters / EARTH_RADIUS_METERS;

        double sinLat1 = Math.sin(lat1);
        double cosLat1 = Math.cos(lat1);
        double sinAd = Math.sin(angularDistance);
        double cosAd = Math.cos(angularDistance);

        double lat2 = Math.asin(
                sinLat1 * cosAd +
                        cosLat1 * sinAd * Math.cos(bearing)
        );

        double lon2 = lon1 + Math.atan2(
                Math.sin(bearing) * sinAd * cosLat1,
                cosAd - sinLat1 * Math.sin(lat2)
        );

        // Normalize longitude to [-180, 180)
        double lon2Deg = Math.toDegrees(lon2);
        lon2Deg = ((lon2Deg + 540.0) % 360.0) - 180.0;

        double lat2Deg = Math.toDegrees(lat2);

        return new Location(lon2Deg, lat2Deg);
    }

    /**
     * Calculates the distance between two locations in meters using the Haversine formula.
     *
     * @param a first location
     * @param b second location
     * @return distance in meters
     */
    public static long distance(Location a, Location b) {
        if (a == null || b == null || a.getCoordinates() == null || b.getCoordinates() == null) {
            return -1;
        }


        double lon1 = a.getLon();
        double lat1 = a.getLat();
        double lon2 = b.getLon();
        double lat2 = b.getLat();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double aVal = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));
        return Math.round(EARTH_RADIUS_METERS * c);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return java.util.Objects.equals(coordinates, location.coordinates);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(coordinates);
    }
}
