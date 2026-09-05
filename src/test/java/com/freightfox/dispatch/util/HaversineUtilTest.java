package com.freightfox.dispatch.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaversineUtilTest {

    @Test
    void testIdenticalPoints() {
        double distance = HaversineUtil.distanceKm(12.9716, 77.5946, 12.9716, 77.5946);
        assertThat(distance).isEqualTo(0.0, within(1e-6));
    }

    @Test
    void testLondonToParis() {
        double distance = HaversineUtil.distanceKm(51.5074, -0.1278, 48.8566, 2.3522);
        assertThat(distance).isCloseTo(343.0, within(5.0));
    }

    @Test
    void testBangaloreToDelhi() {
        double distance = HaversineUtil.distanceKm(12.9716, 77.5946, 28.6139, 77.2090);
        assertThat(distance).isCloseTo(1740.0, within(20.0));
    }

    @Test
    void testAntipodalPoints() {
        double distance = HaversineUtil.distanceKm(0.0, 0.0, 0.0, 180.0);
        assertThat(Double.isNaN(distance)).isFalse();
        assertThat(Double.isInfinite(distance)).isFalse();
        assertThat(distance).isCloseTo(20015.0, within(20.0));
    }

    @Test
    void testDegreeToRadianConversion() {
        double distance = HaversineUtil.distanceKm(0.0, 0.0, 90.0, 0.0);
        assertThat(distance).isCloseTo(10007.5, within(5.0));
    }

    @Test
    void testFormatDistance() {
        assertThat(HaversineUtil.formatDistance(5.0)).isEqualTo("5 km");
        assertThat(HaversineUtil.formatDistance(5.234)).isEqualTo("5.23 km");
        assertThat(HaversineUtil.formatDistance(0.0)).isEqualTo("0 km");
    }
}
