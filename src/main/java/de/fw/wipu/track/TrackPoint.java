package de.fw.wipu.track;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.fw.wipu.Location;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class TrackPoint {

    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    @JsonIgnore
    private String id;

    @JsonIgnore
    @BsonProperty("trackId")
    private ObjectId trackId;

    @BsonProperty("location")
    @JsonUnwrapped
    private Location location;

    @BsonProperty("time")
    @JsonProperty(value = "time", required = true)
    private LocalDateTime time;

    @BsonProperty("elevation")
    @JsonProperty(value = "elevation")
    private double elevation;

    public TrackPoint() {
    }

    public TrackPoint(Location location, LocalDateTime time, double elevation) {
        this.location = location;
        this.time = time;
        this.elevation = elevation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ObjectId getTrackId() {
        return trackId;
    }

    public void setTrackId(ObjectId trackId) {
        this.trackId = trackId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackPoint that = (TrackPoint) o;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
