package de.fw.wipu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Location {
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
}
