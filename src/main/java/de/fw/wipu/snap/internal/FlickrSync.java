package de.fw.wipu.snap.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fw.wipu.snap.Snap;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Fetches photos from Flickr and stores them in MongoDB.
 * @RestClient tutorial looked promising, but in the end it does not help too much
 */
@ApplicationScoped
public class FlickrSync {

    @Inject
    FlickrConfig flickrConfig;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;


    private static final String FLICKR_SEARCH_URL = """
            https://www.flickr.com/services/rest/?method=flickr.photos.search\
            &api_key=%s\
            &user_id=%s\
            &has_geo=1\
            &extras=date_taken,geo,url_t,url_l\
            &min_taken_date=2013-01-01\
            &format=json\
            &nojsoncallback=1""";

    @Inject
    ReactiveMongoClient mongoClient;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    
    
    public Uni<Void> sync() {
        String searchUrl = FLICKR_SEARCH_URL.formatted(flickrConfig.apiKey(), flickrConfig.userId());

        HttpClient http = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(searchUrl))
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        return Uni.createFrom()
                .completionStage(() -> http.sendAsync(req, HttpResponse.BodyHandlers.ofString()))
                .onItem().transform(HttpResponse::body)
                .onItem().transform(this::parsePhotos)
                .onItem().transform(this::mapToSnaps)
                .onItem().transformToUni(snaps -> snaps.isEmpty()
                        ? Uni.createFrom().voidItem()
                        : getCollection().insertMany(snaps).replaceWithVoid());
    }

    private List<Snap> mapToSnaps(List<Photo> photos) {
        return photos.stream()
                .filter(Objects::nonNull)
                .map(p -> new Snap(
                        null, // let Mongo assign ObjectId
                        nullToEmpty(p.title),
                        parseLongSafe(p.id),
                        p.secret,
                        p.server,
                        parseDouble(p.latitude),
                        parseDouble(p.longitude),
                        p.thumbNailWidth,
                        p.thumbNailHeight,
                        p.imageWidth,
                        p.imageHeight,
                        parseLocalDateTime(p.dateTaken)
                ))
                .collect(Collectors.toList());
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }
    private static Long parseLongSafe(String s) {
        try { return s == null ? null : Long.parseLong(s); } catch (NumberFormatException e) { return null; }
    }
    private static Double parseDouble(String s) {
        try { return s == null || s.isBlank() ? null : Double.parseDouble(s); } catch (Exception e) { return null; }
    }

    private static LocalDateTime parseLocalDateTime(String s) {
        try {
            return s == null || s.isBlank() ? null : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }

    private List<Photo> parsePhotos(String json) {
        try {
            PhotosResponse pr = mapper.readValue(json, PhotosResponse.class);
            if (pr == null || pr.photos == null || pr.photos.photo == null) return List.of();
            return pr.photos.photo;
        } catch (Exception e) {
            return List.of();
        }
    }

    private ReactiveMongoCollection<Snap> getCollection() {
        return mongoClient.getDatabase(databaseName).getCollection(Snap.SNAP_COLLECTION_NAME, Snap.class);
    }

    // DTOs for Flickr response
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PhotosResponse {
        @JsonProperty("photos") Photos photos;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Photos {
        @JsonProperty("photo") List<Photo> photo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Photo {
        @JsonProperty("id") String id;
        @JsonProperty("title") String title;
        @JsonProperty("secret") String secret;
        @JsonProperty("server") String server;
        @JsonProperty("latitude") String latitude;
        @JsonProperty("longitude") String longitude;
        @JsonProperty("datetaken")
        String dateTaken;
        @JsonProperty("width_t")
        Integer thumbNailWidth;
        @JsonProperty("height_t")
        Integer thumbNailHeight;
        @JsonProperty("width_l")
        Integer imageWidth;
        @JsonProperty("height_l")
        Integer imageHeight;


    }
}
