package de.fw.wipu.geohash.internal;

import de.fw.wipu.Location;
import de.fw.wipu.geohash.GeoHash;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class GeoHashCronJob {

    private static final Location SCHWABACH = new Location(11.020650, 49.329109);
    private static final Location CHECINY = new Location(20.465209, 50.799461);
    private static final List<Location> LOCATIONS = List.of(SCHWABACH, CHECINY);
    private static final Logger LOG = Logger.getLogger(GeoHashCronJob.class);

    @Inject
    Mailer mailer;

    @Inject
    GeoHashWorker geoHashWorker;

    @Scheduled(cron = "0 30 15 * * ?")
    public void runJob() {
        LOG.info("Running GeoHashCronJob");
        try {
            LOCATIONS.forEach(l -> {
                LocalDate today = LocalDate.now();
                GeoHash geoHash = geoHashWorker.hashPointFor(l.getLat().intValue(), l.getLon().intValue(), today);
                Set<Location> locations = geoHashWorker.hashPointsWithinReach(l, geoHash.getLocation(), 25);
                if (!locations.isEmpty()) {
                    // Note: only _we_ know here that there may be only one GeoHash for now (because of the 25km limit)
                    Location hashPoint = locations.iterator().next();
                    mailer.send(Mail.withText("fw@123k.org",
                            String.format("GeoHash for %s is near!", today),
                            String.format("There are GeoHashes near today. Please go fast to https://123k.org/geohashing/%s/%s/.\n\n Sincerely your's WIPU", hashPoint.getGraticule().getLat().intValue(), hashPoint.getGraticule().getLon().intValue())));
                }
            });

        } catch (Exception e) {
            LOG.error("Failed to run GeoHashCronJob", e);
        }
    }

}
