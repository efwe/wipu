package de.fw.wipu.geohash;

import de.fw.wipu.geohash.internal.GeoHashWorker;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response geoHash() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Use /geohash/{lat}/{lon}/{date} (YYYY-MM-DD)")
                .build();
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
}
