package de.fw.wipu.geohash;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.fw.wipu.Location;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDate;

public class GeoHash {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date; // date only, no time

    @JsonUnwrapped
    private Location location;

    @JsonProperty("djia")
    private String djia;

    public GeoHash(Location location, LocalDate date, String djia) {
        this.location = location;
        this.date = date;
        this.djia = djia;
    }

    public Location getLocation() {
        return location;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDjia() {
        return djia;
    }
}
