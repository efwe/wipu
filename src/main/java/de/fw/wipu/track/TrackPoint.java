package de.fw.wipu.track;

import de.fw.wipu.Location;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class TrackPoint {

    @BsonId
    private ObjectId id;

    private ObjectId trackId;

    private Location location;
    private LocalDateTime time;
    private double elevation;

    public TrackPoint() {
    }

    public TrackPoint(Location location, LocalDateTime time, double elevation) {
        this.location = location;
        this.time = time;
        this.elevation = elevation;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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
