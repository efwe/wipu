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
    @BsonProperty("type")
    private String type = "Point";

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
        return new Double[]{coordinates.get(1), coordinates.get(0)};
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
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

        double lon1 = a.getCoordinates().get(0);
        double lat1 = a.getCoordinates().get(1);
        double lon2 = b.getCoordinates().get(0);
        double lat2 = b.getCoordinates().get(1);

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double aVal = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(aVal), Math.sqrt(1 - aVal));
        return Math.round(EARTH_RADIUS_METERS * c);
    }
}
