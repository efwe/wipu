package de.fw.wipu.geohash.internal;

import de.fw.wipu.geohash.GeoHash;
import jakarta.enterprise.context.ApplicationScoped;
import de.fw.wipu.Location;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@ApplicationScoped
public class GeoHashWorker {

    public GeoHash hashPointFor(int lat, int lon, LocalDate date) {
        String djia = djiaFor(lon, date);
        String seed = String.format("%04d-%02d-%02d-%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth(), djia);
        return new GeoHash(locationFor(lat, lon, seed ), date, djia);
    }

    Location locationFor(int lat, int lon, String seed) {

        String md5 = md5Hex(seed);
        String a = md5.substring(0, 16);
        String b = md5.substring(16, 32);

        double fracLat = hex16ToUnitFraction(a);
        double fracLon = hex16ToUnitFraction(b);

        // Always append fraction to the integer part, preserving the sign of the integer graticule
        double resultLat = lat + Math.copySign(fracLat, lat);
        double resultLon = lon + Math.copySign(fracLon, lon);

        return new Location(resultLon, resultLat);
    }

    /**
     * first djia implementation with geo.crox.net
     * Note: bank-holidays missing
     */
    String djiaFor(int lon, LocalDate date) {
        if (date == null) throw new IllegalArgumentException("date must not be null");

        // Apply 30W rule: For longitudes <= -30, use previous day's DJIA for dates on/after 2008-05-27
        LocalDate effectiveDate = apply30WRule(lon, date);

        // Use the public geohashing DJIA proxy service
        String url = String.format("http://geo.crox.net/djia/%04d-%02d-%02d", effectiveDate.getYear(), effectiveDate.getMonthValue(), effectiveDate.getDayOfMonth());


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Failed to fetch DJIA: HTTP " + response.statusCode());
            }
            String body = response.body();
            if (body == null) {
                throw new IllegalStateException("Empty response from DJIA service");
            }
            String value = body.trim();
            // The service typically returns a plain decimal string like 12345.67
            // Validate basic format and return as-is for hashing seed
            if (!value.matches("^[0-9]+\\.[0-9]+$")) {
                throw new IllegalStateException("Unexpected DJIA format: '" + value + "'");
            }
            return value;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching DJIA", ie);
        } catch (Exception e) {
            throw new IllegalStateException("Error retrieving DJIA from geo.crox.net", e);
        }
    }


    private LocalDate apply30WRule(int lon, LocalDate date) {
        // 30W rule took effect after the 2008-05-26 change; use previous day's DJIA west of 30W
        LocalDate cutoff = LocalDate.of(2008, 5, 27);
        if (lon >= -30 && !date.isBefore(cutoff)) {
            return date.minusDays(1);
        }
        return date;
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // Convert to 32-char zero-padded hex
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
    }

    /**
     * Convert a 16-hex-digit string to a fraction in [0,1).
     */
    private static double hex16ToUnitFraction(String hex16) {
        if (hex16 == null || hex16.length() != 16) {
            throw new IllegalArgumentException("Expected 16 hex chars");
        }
        BigInteger value = new BigInteger(hex16, 16);
        // denominator = 16^16
        double denom = Math.pow(16.0, 16.0);
        return value.doubleValue() / denom;
    }
}
