package com.fujitsu.deliveryfee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.repository.WeatherDataRepository;
import com.fujitsu.deliveryfee.service.weather.WeatherClient;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherImportServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private WeatherDataRepository weatherDataRepository;

    private WeatherImportService weatherImportService;

    @BeforeEach
    void setUp() {
        weatherImportService = new WeatherImportService(weatherClient, weatherDataRepository);
    }

    @Test
    void shouldPersistEachImportedObservationWithoutOverwritingHistory() {
        List<WeatherData> firstBatch = List.of(
                weather(City.TALLINN, "Tallinn-Harku", "26038", OffsetDateTime.parse("2026-03-30T10:15:00Z")),
                weather(City.TARTU, "Tartu-Toravere", "26242", OffsetDateTime.parse("2026-03-30T10:15:00Z")),
                weather(City.PARNU, "P\u00e4rnu", "41803", OffsetDateTime.parse("2026-03-30T10:15:00Z"))
        );
        List<WeatherData> secondBatch = List.of(
                weather(City.TALLINN, "Tallinn-Harku", "26038", OffsetDateTime.parse("2026-03-30T11:15:00Z")),
                weather(City.TARTU, "Tartu-Toravere", "26242", OffsetDateTime.parse("2026-03-30T11:15:00Z")),
                weather(City.PARNU, "P\u00e4rnu", "41803", OffsetDateTime.parse("2026-03-30T11:15:00Z"))
        );

        when(weatherClient.fetchCurrentWeather()).thenReturn(firstBatch, secondBatch);

        weatherImportService.importCurrentWeather();
        weatherImportService.importCurrentWeather();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<WeatherData>> captor = ArgumentCaptor.forClass((Class) List.class);
        verify(weatherDataRepository, times(2)).saveAll(captor.capture());

        assertEquals(firstBatch, captor.getAllValues().get(0));
        assertEquals(secondBatch, captor.getAllValues().get(1));
    }

    private WeatherData weather(City city, String stationName, String wmoCode, OffsetDateTime observedAt) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setObservedAt(observedAt);
        weatherData.setStationName(stationName);
        weatherData.setWmoCode(wmoCode);
        weatherData.setAirTemperature(1.2);
        weatherData.setWindSpeed(4.5);
        weatherData.setWeatherPhenomenon("Light rain");
        weatherData.setSource("TEST");
        return weatherData;
    }
}
