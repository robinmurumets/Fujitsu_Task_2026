package com.fujitsu.deliveryfee.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.weather")
public class WeatherApiProperties {

    private String cron;
    private String observationsUrl;

    /**
     * Returns the cron expression used for scheduled imports.
     */
    public String getCron() {
        return cron;
    }

    /**
     * Sets the cron expression used for scheduled imports.
     */
    public void setCron(String cron) {
        this.cron = cron;
    }

    /**
     * Returns the XML observations feed URL.
     */
    public String getObservationsUrl() {
        return observationsUrl;
    }

    /**
     * Sets the XML observations feed URL.
     */
    public void setObservationsUrl(String observationsUrl) {
        this.observationsUrl = observationsUrl;
    }
}

