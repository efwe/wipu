package de.fw.wipu.track;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrackInput {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "description", required = true)
    private String description;

    public TrackInput() {
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
}
