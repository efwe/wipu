package de.fw.wipu.track;

import de.fw.wipu.track.internal.TrackWorker;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

/**
 * Placeholder for the TrackResource.
 */
@Path("/tracks")
@RolesAllowed("infra")
public class TrackResource {

    @Inject
    TrackWorker trackWorker;

    @jakarta.ws.rs.GET
    public String placeholder() {
        return "Track API placeholder";
    }

}
