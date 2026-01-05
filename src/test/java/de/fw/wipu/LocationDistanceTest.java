package de.fw.wipu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationDistanceTest {

    @Test
    public void testDistance() {
        // Berlin: 52.5200, 13.4050
        Location berlin = new Location(13.4050, 52.5200);
        // Munich: 48.1351, 11.5820
        Location munich = new Location(11.5820, 48.1351);

        long dist = Location.distance(berlin, munich);
        
        // Distance between Berlin and Munich is roughly 504 km
        assertEquals(504117, dist, 1000); // Allow 1km deviation
    }

    @Test
    public void testDistanceSamePoint() {
        Location a = new Location(13.4050, 52.5200);
        assertEquals(0, Location.distance(a, a));
    }
}
