package com.fujitsu.deliveryfee.service;

import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.repository.WeatherDataRepository;
import com.fujitsu.deliveryfee.service.weather.WeatherClient;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeatherImportService {

    private final WeatherClient weatherClient;
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Creates the import service with the configured weather client and repository.
     */
    public WeatherImportService(WeatherClient weatherClient, WeatherDataRepository weatherDataRepository) {
        this.weatherClient = weatherClient;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Imports the latest XML observation snapshot and persists every station entry as a new history row.
     */
    @Transactional
    public void importCurrentWeather() {
        List<WeatherData> weatherEntries = weatherClient.fetchCurrentWeather();
        weatherDataRepository.saveAll(weatherEntries);
    }
}

