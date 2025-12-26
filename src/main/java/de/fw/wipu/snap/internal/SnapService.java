package de.fw.wipu.snap.internal;


import de.fw.wipu.BoundingBox;
import de.fw.wipu.snap.Snap;
import com.mongodb.client.model.Filters;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class SnapService {

    @Inject
    ReactiveMongoClient mongoClient;

    @ConfigProperty(name = "quarkus.mongodb.database")
    String databaseName;

    public Uni<List<Snap>> list() {
        return getCollection().find().collect().asList();
    }

    public Uni<List<Snap>> findByBoundingBox(BoundingBox bbox) {
        return getCollection().find(
                Filters.geoWithinBox("location",
                        bbox.getSouthWest().getCoordinates().get(0),
                        bbox.getSouthWest().getCoordinates().get(1),
                        bbox.getNorthEast().getCoordinates().get(0),
                        bbox.getNorthEast().getCoordinates().get(1)
                )
        ).collect().asList();
    }


    private ReactiveMongoCollection<Snap> getCollection() {
        return mongoClient.getDatabase(databaseName).getCollection(Snap.SNAP_COLLECTION_NAME, Snap.class);
    }
}
