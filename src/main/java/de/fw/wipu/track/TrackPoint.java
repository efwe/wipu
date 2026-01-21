package de.fw.wipu.track;

import de.fw.wipu.Location;
import java.time.LocalDateTime;

public class TrackPoint {
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
}
