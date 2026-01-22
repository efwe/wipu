package de.fw.wipu.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.fw.wipu.BoundingBox;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Model for a GPS track.
 */
public class Track {

    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    @BsonProperty("title")
    @JsonProperty(value = "title", required = true)
    private String title;

    @BsonProperty("description")
    @JsonProperty(value = "description", required = true)
    private String description;

    @BsonProperty("startTime")
    @JsonProperty(value = "startTime", required = true)
    private LocalDateTime startTime;

    @BsonProperty("endTime")
    @JsonProperty(value = "endTime", required = true)
    private LocalDateTime endTime;

    @BsonProperty("distance")
    @JsonProperty(value = "distance", required = true)
    private Double distance;

    @BsonProperty("boundingBox")
    @JsonProperty(value = "boundingBox", required = true)
    private BoundingBox boundingBox;

    @BsonIgnore
    @JsonProperty(value = "trackPoints", required = true)
    private List<TrackPoint> trackPoints;

    public Track() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
