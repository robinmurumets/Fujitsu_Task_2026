package com.fujitsu.deliveryfee.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherScheduler.class);

    private final WeatherImportService weatherImportService;

    /**
     * Creates the scheduler with the weather import service.
     */
    public WeatherScheduler(WeatherImportService weatherImportService) {
        this.weatherImportService = weatherImportService;
    }

    /**
     * Runs the first weather import after startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initialImport() {
        runImport("startup");
    }

    /**
     * Runs the weather import on the configured schedule.
     */
    @Scheduled(cron = "${app.weather.cron}")
    public void scheduledImport() {
        runImport("scheduled");
    }

    private void runImport(String trigger) {
        try {
            weatherImportService.importCurrentWeather();
            log.info("Weather import finished successfully ({})", trigger);
        } catch (RuntimeException exception) {
            log.error("Weather import failed ({})", trigger, exception);
        }
    }
}
