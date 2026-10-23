package io.github.sumyabazar1.weather;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HomePanelSnapshotTest {
    @Test
    public void setupAndOfflineSnapshotsOnlyCarryTheirState() {
        HomePanelSnapshot noCity = HomePanelSnapshot.noCity();
        assertEquals(HomePanelService.NO_CITY, noCity.status);
        assertNull(noCity.city);

        HomePanelSnapshot offline = HomePanelSnapshot.offline();
        assertEquals(HomePanelService.OFFLINE, offline.status);
        assertNull(offline.city);
    }

    @Test
    public void readySnapshotCarriesCurrentConditionsAndCapacityInputsOnly() {
        Forecast.Report report = new Forecast.Report(23, 25, 3, true,
                Collections.singletonList(new Forecast.Day(LocalDate.parse("2026-10-24"), 95, 27, 18)),
                12, 78, 6);

        HomePanelSnapshot snapshot = HomePanelSnapshot.ready("São Paulo", report);

        assertEquals(HomePanelService.READY, snapshot.status);
        assertEquals("São Paulo", snapshot.city);
        assertEquals(23, snapshot.tempC);
        assertEquals(25, snapshot.feelsC);
        assertEquals(3, snapshot.code);
        assertTrue(snapshot.day);
        assertEquals(78, snapshot.humidity);
        assertEquals(6, snapshot.windKmh);
        // Forecast and AQI remain on the Report for the Weather app, but are excluded from the Home IPC model.
        assertFalse(hasField("days"));
        assertFalse(hasField("aqi"));
    }

    private static boolean hasField(String name) {
        try {
            HomePanelSnapshot.class.getDeclaredField(name);
            return true;
        } catch (NoSuchFieldException expected) {
            return false;
        }
    }
}
