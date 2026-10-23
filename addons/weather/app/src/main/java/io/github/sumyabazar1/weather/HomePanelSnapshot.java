package io.github.sumyabazar1.weather;

import android.os.Bundle;

/** Small IPC value object for the Home panel; intentionally excludes forecast days and air quality. */
final class HomePanelSnapshot {
    static final int NO_CITY = 1;
    static final int OFFLINE = 2;
    static final int READY = 3;

    final int status;
    final String city;
    final int tempC;
    final int feelsC;
    final int code;
    final boolean day;
    final int humidity;
    final int windKmh;

    private HomePanelSnapshot(int status, String city, int tempC, int feelsC, int code, boolean day,
                              int humidity, int windKmh) {
        this.status = status;
        this.city = city;
        this.tempC = tempC;
        this.feelsC = feelsC;
        this.code = code;
        this.day = day;
        this.humidity = humidity;
        this.windKmh = windKmh;
    }

    static HomePanelSnapshot noCity() {
        return new HomePanelSnapshot(NO_CITY, null, 0, 0, 0, false, 0, 0);
    }

    static HomePanelSnapshot offline() {
        return new HomePanelSnapshot(OFFLINE, null, 0, 0, 0, false, 0, 0);
    }

    static HomePanelSnapshot ready(String city, Forecast.Report report) {
        return new HomePanelSnapshot(READY, city, report.tempC, report.feelsC, report.code, report.day,
                report.humidity, report.windKmh);
    }

    Bundle toBundle() {
        Bundle out = new Bundle();
        out.putInt(HomePanelService.KEY_STATUS, status);
        if (status == READY) {
            out.putString(HomePanelService.KEY_CITY, city);
            out.putInt(HomePanelService.KEY_TEMP, tempC);
            out.putInt(HomePanelService.KEY_FEELS, feelsC);
            out.putInt(HomePanelService.KEY_CODE, code);
            out.putBoolean(HomePanelService.KEY_DAY, day);
            out.putInt(HomePanelService.KEY_HUMIDITY, humidity);
            out.putInt(HomePanelService.KEY_WIND, windKmh);
        }
        return out;
    }
}
