package com.fujitsu.deliveryfee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fujitsu.deliveryfee.config.DeliveryFeeProperties;
import com.fujitsu.deliveryfee.dto.DeliveryFeeRequest;
import com.fujitsu.deliveryfee.dto.DeliveryFeeResponse;
import com.fujitsu.deliveryfee.exception.FeeCalculationException;
import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.model.WeatherPhenomenon;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryFeeServiceTest {

    @Mock
    private WeatherDataService weatherDataService;

    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    void setUp() {
        deliveryFeeService = new DeliveryFeeService(weatherDataService, properties());
    }

    @Test
    void shouldCalculateBaseFeePlusTemperatureAndRainSurcharge() {
        WeatherData weather = weather(-5.0, 6.0, WeatherPhenomenon.RAIN);
        when(weatherDataService.getWeather(City.TALLINN, null)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.TALLINN, VehicleType.SCOOTER);

        DeliveryFeeResponse response = deliveryFeeService.calculateFee(request);

        assertEquals(new BigDecimal("4.50"), response.fee());
    }

    @Test
    void shouldApplyWindSurchargeForBike() {
        WeatherData weather = weather(2.0, 15.0, WeatherPhenomenon.CLEAR);
        when(weatherDataService.getWeather(City.TARTU, null)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.TARTU, VehicleType.BIKE);

        DeliveryFeeResponse response = deliveryFeeService.calculateFee(request);

        assertEquals(new BigDecimal("3.00"), response.fee());
    }

    @Test
    void shouldNotApplyWindSurchargeAtExactlyTenMetersPerSecond() {
        WeatherData weather = weather(2.0, 10.0, WeatherPhenomenon.CLEAR);
        when(weatherDataService.getWeather(City.TARTU, null)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.TARTU, VehicleType.BIKE);

        DeliveryFeeResponse response = deliveryFeeService.calculateFee(request);

        assertEquals(new BigDecimal("2.50"), response.fee());
    }

    @Test
    void shouldRejectBikeWhenWindIsTooStrong() {
        WeatherData weather = weather(1.0, 25.0, WeatherPhenomenon.CLEAR);
        when(weatherDataService.getWeather(City.PARNU, null)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.PARNU, VehicleType.BIKE);

        FeeCalculationException exception =
                assertThrows(FeeCalculationException.class, () -> deliveryFeeService.calculateFee(request));

        assertEquals("Usage of selected vehicle type is forbidden", exception.getMessage());
    }

    @Test
    void shouldRejectScooterDuringForbiddenPhenomenon() {
        WeatherData weather = weather(-2.0, 4.0, WeatherPhenomenon.THUNDER);
        when(weatherDataService.getWeather(City.TALLINN, null)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.TALLINN, VehicleType.SCOOTER);

        assertThrows(FeeCalculationException.class, () -> deliveryFeeService.calculateFee(request));
    }

    @Test
    void shouldUseHistoricalTimestampWhenProvided() {
        OffsetDateTime at = OffsetDateTime.parse("2026-03-30T10:15:00Z");
        WeatherData weather = weather(-12.0, 4.0, WeatherPhenomenon.SNOW);
        weather.setObservedAt(at.minusMinutes(5));
        when(weatherDataService.getWeather(City.TARTU, at)).thenReturn(weather);

        DeliveryFeeRequest request = request(City.TARTU, VehicleType.BIKE);
        request.setAt(at);

        DeliveryFeeResponse response = deliveryFeeService.calculateFee(request);

        assertEquals(new BigDecimal("4.50"), response.fee());
        assertEquals(at.minusMinutes(5), response.weatherObservedAt());
    }

    private DeliveryFeeRequest request(City city, VehicleType vehicleType) {
        DeliveryFeeRequest request = new DeliveryFeeRequest();
        request.setCity(city);
        request.setVehicleType(vehicleType);
        return request;
    }

    private WeatherData weather(double airTemperature, double windSpeed, WeatherPhenomenon phenomenon) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(City.TALLINN);
        weatherData.setObservedAt(OffsetDateTime.parse("2026-03-30T10:00:00Z"));
        weatherData.setStationName("Tallinn-Harku");
        weatherData.setWmoCode("26038");
        weatherData.setAirTemperature(airTemperature);
        weatherData.setWindSpeed(windSpeed);
        weatherData.setWeatherPhenomenon(phenomenon.name());
        weatherData.setSource("TEST");
        return weatherData;
    }

    private DeliveryFeeProperties properties() {
        DeliveryFeeProperties properties = new DeliveryFeeProperties();

        Map<City, Map<VehicleType, BigDecimal>> baseFees = new EnumMap<>(City.class);
        baseFees.put(City.TALLINN, Map.of(
                VehicleType.CAR, new BigDecimal("4.0"),
                VehicleType.SCOOTER, new BigDecimal("3.5"),
                VehicleType.BIKE, new BigDecimal("3.0")
        ));
        baseFees.put(City.TARTU, Map.of(
                VehicleType.CAR, new BigDecimal("3.5"),
                VehicleType.SCOOTER, new BigDecimal("3.0"),
                VehicleType.BIKE, new BigDecimal("2.5")
        ));
        baseFees.put(City.PARNU, Map.of(
                VehicleType.CAR, new BigDecimal("3.0"),
                VehicleType.SCOOTER, new BigDecimal("2.5"),
                VehicleType.BIKE, new BigDecimal("2.0")
        ));
        properties.setBaseFees(baseFees);

        Map<VehicleType, DeliveryFeeProperties.VehicleRuleProperties> vehicleRules = new EnumMap<>(VehicleType.class);
        vehicleRules.put(VehicleType.SCOOTER, scooterRules());
        vehicleRules.put(VehicleType.BIKE, bikeRules());
        properties.setVehicleRules(vehicleRules);

        return properties;
    }

    private DeliveryFeeProperties.VehicleRuleProperties scooterRules() {
        DeliveryFeeProperties.VehicleRuleProperties rules = new DeliveryFeeProperties.VehicleRuleProperties();
        rules.getTemperature().setBelowMinusTen(new BigDecimal("1.0"));
        rules.getTemperature().setBetweenMinusTenAndZero(new BigDecimal("0.5"));
        rules.getPhenomenon().setSnowOrSleet(new BigDecimal("1.0"));
        rules.getPhenomenon().setRain(new BigDecimal("0.5"));
        rules.getPhenomenon().setForbidden(Set.of(WeatherPhenomenon.GLAZE, WeatherPhenomenon.HAIL, WeatherPhenomenon.THUNDER));
        return rules;
    }

    private DeliveryFeeProperties.VehicleRuleProperties bikeRules() {
        DeliveryFeeProperties.VehicleRuleProperties rules = scooterRules();
        rules.getWind().setAboveTenUpToTwenty(new BigDecimal("0.5"));
        rules.getWind().setForbiddenAbove(new BigDecimal("20.0"));
        return rules;
    }
}

