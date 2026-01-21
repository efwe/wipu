package de.fw.wipu.geohash;

import de.fw.wipu.Location;
import de.fw.wipu.geohash.internal.GeoHashCronJob;
import de.fw.wipu.geohash.internal.GeoHashWorker;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@Path("/geohash")
@Produces(MediaType.APPLICATION_JSON)
public class GeoHashResource {

    @Inject
    GeoHashWorker geoHashWorker;

    @Inject
    GeoHashCronJob geoHashCronJob;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response geoHash() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Use /geohash/{lat}/{lon}/{date} (YYYY-MM-DD)")
                .build();
    }

    /**
     * This is not a general-purpose API - it is specially implemented for my site.
     * The following assumptions are made:
     * <ul>
     *     <li>we are west of -30</li>
     *     <li>the request is made from CET, arrives at CET and assumes that the result is good for CET :)</li>
     * </ul>
     * @return a list of geohashes which is a: empty if no forecast is available b: contains one geohash if the forecast is available on a weekday c: up to 3 points if the request is made on Friday afternoon
     */
    @GET
    @Path("/forecast")
    public Response forecast() {
        return Response.ok(geoHashWorker.forecast()).build();
    }

    @GET
    @Path("/global")
    public Response global() {
        GeoHash geoHash = geoHashWorker.hashPointFor(49, 11, LocalDate.now());
        Location location = geoHashWorker.globalHash(geoHash.getLocation().getLatFraction(), geoHash.getLocation().getLonFraction());
        return Response.ok(location).build();
    }

    @GET
    @Path("/{lat}/{lon}/{date}")
    public Response geoHash(
            @PathParam("lat") int lat,
            @PathParam("lon") int lon,
            @PathParam("date") String dateStr) {

        // Validate latitude and longitude (graticule integers)
        if (lat < -90 || lat > 90) {
            return badRequest("Latitude must be between -90 and 90 inclusive");
        }
        if (lon < -180 || lon > 180) {
            return badRequest("Longitude must be between -180 and 180 inclusive");
        }

        // Parse LocalDate (YYYY-MM-DD), and validate allowed window
        final LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            return badRequest("Date must be in format YYYY-MM-DD");
        }

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (date.isAfter(today.plusDays(3))) {
            return badRequest("Date must not be more than 3 days in the future");
        }
        if (date.isBefore(today.minusYears(3))) {
            return badRequest("Date must not be more than 3 years in the past");
        }

        GeoHash result = geoHashWorker.hashPointFor(lat, lon, date);
        return Response.ok(result).build();
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }

    /**
     * for testing purposes only
     */
    @GET
    @Path("/email")
    @RolesAllowed("infra")
    public Response email() {
        geoHashCronJob.runJob();
        return Response.ok("").build();
    }


}
