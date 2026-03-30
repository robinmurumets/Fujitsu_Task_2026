package com.fujitsu.deliveryfee.service;

import com.fujitsu.deliveryfee.config.DeliveryFeeProperties;
import com.fujitsu.deliveryfee.dto.DeliveryFeeRequest;
import com.fujitsu.deliveryfee.dto.DeliveryFeeResponse;
import com.fujitsu.deliveryfee.exception.FeeCalculationException;
import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import com.fujitsu.deliveryfee.model.WeatherData;
import com.fujitsu.deliveryfee.model.WeatherPhenomenon;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DeliveryFeeService {

    private final WeatherDataService weatherDataService;
    private final DeliveryFeeProperties deliveryFeeProperties;

    /**
     * Creates the service with weather lookup and fee configuration.
     */
    public DeliveryFeeService(WeatherDataService weatherDataService, DeliveryFeeProperties deliveryFeeProperties) {
        this.weatherDataService = weatherDataService;
        this.deliveryFeeProperties = deliveryFeeProperties;
    }

    /**
     * Calculates the delivery fee using the latest weather data or a historical observation when requested.
     */
    public DeliveryFeeResponse calculateFee(DeliveryFeeRequest request) {
        WeatherData weather = weatherDataService.getWeather(request.getCity(), request.getAt());
        BigDecimal fee = baseFeeFor(request.getCity(), request.getVehicleType())
                .add(temperatureExtraFor(request.getVehicleType(), weather))
                .add(windExtraFor(request.getVehicleType(), weather))
                .add(weatherPhenomenonExtraFor(request.getVehicleType(), weather))
                .setScale(2, RoundingMode.HALF_UP);

        return new DeliveryFeeResponse(
                request.getCity(),
                request.getVehicleType(),
                weather.getObservedAt(),
                fee
        );
    }

    private BigDecimal baseFeeFor(City city, VehicleType vehicleType) {
        Map<VehicleType, BigDecimal> cityFees = deliveryFeeProperties.getBaseFees().get(city);
        if (cityFees == null || !cityFees.containsKey(vehicleType)) {
            throw new FeeCalculationException("Base fee is not configured for %s and %s".formatted(city, vehicleType));
        }
        return cityFees.get(vehicleType);
    }

    private BigDecimal temperatureExtraFor(VehicleType vehicleType, WeatherData weather) {
        DeliveryFeeProperties.VehicleRuleProperties rules = rulesFor(vehicleType);
        if (rules == null) {
            return BigDecimal.ZERO;
        }

        double temperature = weather.getAirTemperature();
        if (temperature < -10) {
            return rules.getTemperature().getBelowMinusTen();
        }
        if (temperature <= 0) {
            return rules.getTemperature().getBetweenMinusTenAndZero();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal windExtraFor(VehicleType vehicleType, WeatherData weather) {
        DeliveryFeeProperties.VehicleRuleProperties rules = rulesFor(vehicleType);
        if (rules == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal forbiddenAbove = rules.getWind().getForbiddenAbove();
        double windSpeed = weather.getWindSpeed();
        if (forbiddenAbove != null && windSpeed > forbiddenAbove.doubleValue()) {
            throw new FeeCalculationException("Usage of selected vehicle type is forbidden");
        }
        if (windSpeed > 10) {
            return rules.getWind().getAboveTenUpToTwenty();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal weatherPhenomenonExtraFor(VehicleType vehicleType, WeatherData weather) {
        DeliveryFeeProperties.VehicleRuleProperties rules = rulesFor(vehicleType);
        if (rules == null) {
            return BigDecimal.ZERO;
        }

        WeatherPhenomenon phenomenon = WeatherPhenomenon.fromDescription(weather.getWeatherPhenomenon());
        if (rules.getPhenomenon().getForbidden().contains(phenomenon)) {
            throw new FeeCalculationException("Usage of selected vehicle type is forbidden");
        }
        if (phenomenon.isSnowOrSleet()) {
            return rules.getPhenomenon().getSnowOrSleet();
        }
        if (phenomenon.isRainLike()) {
            return rules.getPhenomenon().getRain();
        }
        return BigDecimal.ZERO;
    }

    private DeliveryFeeProperties.VehicleRuleProperties rulesFor(VehicleType vehicleType) {
        return deliveryFeeProperties.getVehicleRules().get(vehicleType);
    }
}
