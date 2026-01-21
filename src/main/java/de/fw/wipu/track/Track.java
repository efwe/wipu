package de.fw.wipu.track;

import de.fw.wipu.BoundingBox;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model for a GPS track.
 */
public class Track {

    @BsonId
    private ObjectId id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double distance;
    private BoundingBox boundingBox;
    private List<TrackPoint> trackPoints;

    public Track() {
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    public void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return java.util.Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
