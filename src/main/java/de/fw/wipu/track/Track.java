package de.fw.wipu.track;

import de.fw.wipu.BoundingBox;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Model for a GPS track.
 */
public class Track {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BoundingBox boundingBox;
    private List<TrackPoint> trackPoints;

    public Track() {
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
}
