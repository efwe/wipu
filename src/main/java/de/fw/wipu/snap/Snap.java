package de.fw.wipu.snap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.fw.wipu.Location;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

/**
 * flickr.photo.search result example:
 * {
 * "id": "54955548138",
 * "owner": "85707254@N07",
 * "secret": "c6a4215ae9",
 * "server": "65535",
 * "farm": 66,
 * "title": "IMG_20250503_160513",
 * "ispublic": 1,
 * "isfriend": 0,
 * "isfamily": 0,
 * "datetaken": "2025-05-03 16:05:13",
 * "datetakengranularity": 0,
 * "datetakenunknown": "0",
 * "latitude": "47.648355",
 * "longitude": "20.664072",
 * "accuracy": "16",
 * "context": 0,
 * "place_id": "esOd3PNWU7.lxy4",
 * "woeid": "715158",
 * "geo_is_public": 1,
 * "geo_is_contact": 0,
 * "geo_is_friend": 0,
 * "geo_is_family": 0,
 * "upgrade_sizes": [
 * "h",
 * "k",
 * "3k",
 * "4k",
 * "o"
 * ],
 * "url_t": "https://live.staticflickr.com/65535/54955548138_c6a4215ae9_t.jpg",
 * "height_t": 75,
 * "width_t": 100,
 * "url_l": "https://live.staticflickr.com/65535/54955548138_c6a4215ae9_b.jpg",
 * "height_l": 768,
 * "width_l": 1024
 * }
 */
public class Snap {

    public static final String SNAP_COLLECTION_NAME = "snap";

    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String id;

    @BsonProperty("title")
    @JsonProperty(value = "title", required = true)
    private String title;

    @BsonProperty("snapId")
    @JsonProperty(value = "snapId", required = true)
    private Long snapId;

    @BsonProperty("secret")
    @JsonProperty(value = "secret", required = true)
    private String secret;

    @BsonProperty("server")
    @JsonProperty(value = "server")
    private String server;

    @BsonProperty("location")
    @JsonUnwrapped
    private Location location;

    public Snap() {
        // no-arg constructor for frameworks
    }

    public Snap(String id, String title, Long snapId, String secret, String server,
                Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.snapId = snapId;
        this.secret = secret;
        this.server = server;
        if (latitude != null && longitude != null) {
            this.location = new Location(longitude, latitude);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSnapId() {
        return snapId;
    }

    public void setSnapId(Long snapId) {
        this.snapId = snapId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Build the Flickr static URL for the thumbnail (suffix "_t").
     * Example: https://live.staticflickr.com/{server}/{id}_{secret}_t.jpg
     */
    @JsonProperty(value = "thumbNailUrl")
    public String getThumbnailUrl() {
        return buildFlickrUrl("t");
    }

    /**
     * Build the Flickr static URL for the large image (suffix "_b").
     * Example: https://live.staticflickr.com/{server}/{id}_{secret}_b.jpg
     */
    @JsonProperty(value = "imageUrl")
    public String getImageUrl() {
        return buildFlickrUrl("b");
    }

    private String buildFlickrUrl(String sizeSuffix) {
        if (server == null || secret == null || snapId == null) {
            return null;
        }
        // According to https://www.flickr.com/services/api/misc.urls.html
        // Preferred host: live.staticflickr.com
        return "https://live.staticflickr.com/" + server + "/" + snapId + "_" + secret + "_" + sizeSuffix + ".jpg";
    }
}
