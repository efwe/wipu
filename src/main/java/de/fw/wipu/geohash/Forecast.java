package de.fw.wipu.geohash;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record Forecast(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate date,
                       @JsonProperty("latFraction") Double latFraction,
                       @JsonProperty("lonFraction") Double lonFraction) {
    public Forecast(LocalDate date, Double latFraction, Double lonFraction) {
        this.date = date;
        this.latFraction = latFraction;
        this.lonFraction = lonFraction;
    }

    public Forecast(GeoHash geoHash) {
        this(geoHash.getDate(), geoHash.getLocation().getLatFraction(), geoHash.getLocation().getLonFraction());
    }

    @Override
    public LocalDate date() {
        return date;
    }

    @Override
    public Double latFraction() {
        return latFraction;
    }

    @Override
    public Double lonFraction() {
        return lonFraction;
    }
}
