package com.fujitsu.deliveryfee.service;

import com.fujitsu.deliveryfee.exception.ResourceNotFoundException;
import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.repository.WeatherDataRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class WeatherDataService {

    private final WeatherDataRepository weatherDataRepository;

    /**
     * Creates the service with access to stored weather observations.
     */
    public WeatherDataService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Returns the latest weather observation for the city, or the latest observation at or before the requested time.
     */
    public WeatherData getWeather(City city, OffsetDateTime at) {
        if (at == null) {
            return weatherDataRepository.findFirstByCityOrderByObservedAtDesc(city)
                    .orElseThrow(() -> new ResourceNotFoundException("No weather data found for city " + city));
        }

        return weatherDataRepository.findFirstByCityAndObservedAtLessThanEqualOrderByObservedAtDesc(city, at)
                .orElseThrow(() -> new ResourceNotFoundException("No weather data found for city " + city));
    }
}
