package com.fujitsu.deliveryfee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.repository.WeatherDataRepository;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(WeatherDataService.class)
class WeatherDataServiceTest {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private WeatherDataService weatherDataService;

    @Test
    void shouldReturnLatestWeatherForCity() {
        weatherDataRepository.save(weather(City.TALLINN, OffsetDateTime.parse("2026-03-30T09:15:00Z"), "Clear"));
        WeatherData newest = weatherDataRepository.save(
                weather(City.TALLINN, OffsetDateTime.parse("2026-03-30T10:15:00Z"), "Light rain")
        );

        WeatherData result = weatherDataService.getWeather(City.TALLINN, null);

        assertEquals(newest.getObservedAt(), result.getObservedAt());
        assertEquals("Light rain", result.getWeatherPhenomenon());
    }

    @Test
    void shouldReturnLatestHistoricalWeatherAtOrBeforeTimestamp() {
        weatherDataRepository.save(weather(City.TARTU, OffsetDateTime.parse("2026-03-30T09:15:00Z"), "Clear"));
        WeatherData expected = weatherDataRepository.save(
                weather(City.TARTU, OffsetDateTime.parse("2026-03-30T10:15:00Z"), "Light snow")
        );
        weatherDataRepository.save(weather(City.TARTU, OffsetDateTime.parse("2026-03-30T11:15:00Z"), "Light rain"));

        WeatherData result = weatherDataService.getWeather(
                City.TARTU,
                OffsetDateTime.parse("2026-03-30T10:45:00Z")
        );

        assertEquals(expected.getObservedAt(), result.getObservedAt());
        assertEquals("Light snow", result.getWeatherPhenomenon());
    }

    private WeatherData weather(City city, OffsetDateTime observedAt, String phenomenon) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setObservedAt(observedAt);
        weatherData.setStationName(city == City.TALLINN ? "Tallinn-Harku" : city == City.TARTU ? "Tartu-Toravere" : "P\u00e4rnu");
        weatherData.setWmoCode(city == City.TALLINN ? "26038" : city == City.TARTU ? "26242" : "41803");
        weatherData.setAirTemperature(1.0);
        weatherData.setWindSpeed(3.0);
        weatherData.setWeatherPhenomenon(phenomenon);
        weatherData.setSource("TEST");
        return weatherData;
    }
}
