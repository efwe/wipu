package de.fw.wipu.track;

import de.fw.wipu.track.internal.TrackService;
import de.fw.wipu.track.internal.TrackWorker;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Placeholder for the TrackResource.
 */
@Path("/tracks")
@Produces(MediaType.APPLICATION_JSON)
public class TrackResource {

    @Inject
    TrackWorker trackWorker;

    @Inject
    TrackService trackService;

    @GET
    @Path("/")
    public Uni<java.util.List<Track>> getTracks() {
        return trackService.findAllTracks();
    }

    @GET
    @Path("/{objectId}")
    public Uni<Track> getTrack(@PathParam("objectId") String objectId) {
        return trackService.findTrack(objectId);
    }

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("infra")
    public Uni<Track> createTrack(@BeanParam TrackUploadForm form) {
        Objects.requireNonNull(form.gpxFile);
        Objects.requireNonNull(form.gpxFile.uploadedFile());

        return Uni.createFrom().item(Unchecked.supplier(() -> {
                    try (InputStream inputStream = Files.newInputStream(form.gpxFile.uploadedFile())) {
                        Track track = trackWorker.parseTrack(inputStream);
                        track.setTitle(form.metadata.getTitle());
                        track.setDescription(form.metadata.getDescription());
                        return track;
                    } catch (IOException e) {
                        throw new WebApplicationException("Unable to parse GPX", e, Response.Status.INTERNAL_SERVER_ERROR);
                    }
                }))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .flatMap(trackService::save);
    }
}
