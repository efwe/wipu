package de.fw.wipu.track.internal;

import com.mongodb.client.model.Filters;
import de.fw.wipu.track.Track;
import de.fw.wipu.track.TrackPoint;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

/**
 * All the things Mongo persistence of Tracks
 * Motto: I don't use jenetics.jpx and I don't bother with actual GPX at all - all we know is our Track-model
 */
@ApplicationScoped
public class TrackService {

    @Inject
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    public Uni<Track> save(Track track) {
        if (track.getId() == null) {
            track.setId(new ObjectId().toHexString());
        }

        List<TrackPoint> points = track.getTrackPoints();
        if (points != null && !points.isEmpty()) {
            points.forEach(p -> {
                p.setId(new ObjectId().toHexString());
                p.setTrackId(new ObjectId(track.getId()));
            });
        }

        Uni<Void> insertTrack = getTrackCollection().insertOne(track).replaceWithVoid();

        Uni<Void> insertPoints = points != null && !points.isEmpty() ?
                getTrackPointCollection().insertMany(points).replaceWithVoid() :
                Uni.createFrom().voidItem();

        return Uni.combine().all().unis(insertTrack, insertPoints).discardItems()
                .replaceWith(track);
    }

    public Uni<List<Track>> findAllTracks() {
        return getTrackCollection().find().collect().asList();
    }

    public Uni<Track> findTrack(String id) {
        Uni<Track> trackUni = getTrackCollection().find(Filters.eq("_id", new ObjectId(id))).toUni();
        Uni<List<TrackPoint>> pointsUni = getTrackPointCollection().find(Filters.eq("trackId", new ObjectId(id))).collect().asList();

        return Uni.combine().all().unis(trackUni, pointsUni).asTuple()
                .map(tuple -> {
                    Track track = tuple.getItem1();
                    if (track != null) {
                        track.setTrackPoints(tuple.getItem2());
                    }
                    return track;
                });
    }

    public Uni<Void> deleteAll() {
        return Uni.combine().all().unis(
                getTrackCollection().deleteMany(Filters.empty()),
                getTrackPointCollection().deleteMany(Filters.empty())
        ).discardItems();
    }

    private ReactiveMongoCollection<Track> getTrackCollection() {
        return mongoClient.getDatabase(databaseName).getCollection("track", Track.class);
    }

    private ReactiveMongoCollection<TrackPoint> getTrackPointCollection() {
        return mongoClient.getDatabase(databaseName).getCollection("trackPoint", TrackPoint.class);
    }
}
