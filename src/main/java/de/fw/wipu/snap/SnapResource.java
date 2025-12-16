package de.fw.wipu.snap;

import de.fw.wipu.snap.internal.FlickrSync;
import de.fw.wipu.snap.internal.SnapService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/snaps")
public class SnapResource {

    @Inject
    SnapService snapService;

    @Inject
    FlickrSync flickrSync;

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<Snap>> snaps() {
        return snapService.list();
    }


    @GET
    @RolesAllowed("infra")
    @Path("/sync")
    public Uni<Void> snapSync() {
        return flickrSync.sync();
    }
}
