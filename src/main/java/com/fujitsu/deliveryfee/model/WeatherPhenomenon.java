package com.fujitsu.deliveryfee.model;

public enum WeatherPhenomenon {
    CLEAR,
    RAIN,
    SNOW,
    SLEET,
    GLAZE,
    HAIL,
    THUNDER,
    OTHER;

    /**
     * Returns whether the phenomenon belongs to the snow or sleet surcharge group.
     */
    public boolean isSnowOrSleet() {
        return this == SNOW || this == SLEET;
    }

    /**
     * Returns whether the phenomenon belongs to the rain surcharge group.
     */
    public boolean isRainLike() {
        return this == RAIN;
    }

    /**
     * Maps the stored weather description to the fee calculation category.
     */
    public static WeatherPhenomenon fromDescription(String description) {
        if (description == null || description.isBlank()) {
            return CLEAR;
        }

        String normalized = description.trim().toLowerCase();
        if (normalized.contains("thunder")) {
            return THUNDER;
        }
        if (normalized.contains("hail")) {
            return HAIL;
        }
        if (normalized.contains("glaze")) {
            return GLAZE;
        }
        if (normalized.contains("sleet")) {
            return SLEET;
        }
        if (normalized.contains("snow")) {
            return SNOW;
        }
        if (normalized.contains("rain")) {
            return RAIN;
        }
        return CLEAR;
    }
}
