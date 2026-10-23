package com.gathin.tvhome;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class HomeWeatherTextTest {
    @Test
    public void weatherCodesMapToLocalizedResourceIds() {
        assertEquals(R.string.home_sky_clear_day, HomeWeatherText.descriptionResource(0, true));
        assertEquals(R.string.home_sky_clear_night, HomeWeatherText.descriptionResource(1, false));
        assertEquals(R.string.home_sky_partly_cloudy, HomeWeatherText.descriptionResource(2, true));
        assertEquals(R.string.home_sky_fog, HomeWeatherText.descriptionResource(48, true));
        assertEquals(R.string.home_sky_rain, HomeWeatherText.descriptionResource(61, true));
        assertEquals(R.string.home_sky_snow, HomeWeatherText.descriptionResource(71, true));
        assertEquals(R.string.home_sky_storm, HomeWeatherText.descriptionResource(95, true));
        assertEquals(R.string.home_sky_cloudy, HomeWeatherText.descriptionResource(42, true));
    }

    @Test
    public void currentLineFormatsTheConditionTextInsteadOfItsResourceNumber() {
        assertEquals("São Paulo · 23° · Cloudy", HomeWeatherText.current(
                "%1$s · %2$d° · %3$s", Locale.US, "São Paulo", 23, "Cloudy"));
    }
}
