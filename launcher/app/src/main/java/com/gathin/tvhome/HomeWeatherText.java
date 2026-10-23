package com.gathin.tvhome;

import java.util.Locale;

/** Weather-code labels and current-condition formatting shared by the Home panel. */
final class HomeWeatherText {
    private HomeWeatherText() { }

    static int descriptionResource(int code, boolean day) {
        if (code == 0 || code == 1) return day ? R.string.home_sky_clear_day : R.string.home_sky_clear_night;
        if (code == 2) return R.string.home_sky_partly_cloudy;
        if (code == 45 || code == 48) return R.string.home_sky_fog;
        if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) return R.string.home_sky_rain;
        if ((code >= 71 && code <= 77) || code == 85 || code == 86) return R.string.home_sky_snow;
        if (code >= 95) return R.string.home_sky_storm;
        return R.string.home_sky_cloudy;
    }

    /** The condition is deliberately a String, so a resource ID cannot be formatted as a numeric label. */
    static String current(String template, Locale locale, String city, int tempC, String condition) {
        return String.format(locale, template, city, tempC, condition);
    }
}
