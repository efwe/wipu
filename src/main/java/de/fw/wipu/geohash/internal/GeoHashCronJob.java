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
import java.time.LocalDateTime;

@ApplicationScoped
public class GeoHashCronJob {

    private static final Logger LOG = Logger.getLogger(GeoHashCronJob.class);

    @Inject
    Mailer mailer;

    @Inject
    GeoHashWorker geoHashWorker;

    @Scheduled(cron = "0 30 15 * * ?")
    public void runJob() {
        LOG.info("Running GeoHashCronJob");
        try {
            LocalDate today = LocalDate.now();

            // Graticule 50, 20
            Location target1 = new Location(20.0, 50.0);
            GeoHash hash1 = geoHashWorker.hashPointFor(50, 20, today);
            checkAndSend(hash1, target1, "50/20");

            // Graticule 49, 11
            Location target2 = new Location(11.0, 49.0);
            GeoHash hash2 = geoHashWorker.hashPointFor(49, 11, today);
            checkAndSend(hash2, target2, "49/11");

        } catch (Exception e) {
            LOG.error("Failed to run GeoHashCronJob", e);
        }
    }

    private void checkAndSend(GeoHash hash, Location target, String label) {
        long distance = Location.distance(hash.getLocation(), target);
        LOG.infof("Distance to %s hashpoint is %d meters", label, distance);

        if (distance < 25000) {
            String recipient = "fw@123k.org";
            mailer.send(Mail.withText(recipient,
                    String.format("GeoHash for %s is near!", label),
                    String.format("The GeoHash for %s today is at %s. Distance: %d meters. The GeoHash Cron Job ran at %s",
                            label, hash.getLocation().getCoordinates(), distance, LocalDateTime.now())));
            LOG.info("Email sent successfully for " + label + " to " + recipient);
        }
    }
}
