package com.fujitsu.deliveryfee.config;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import com.fujitsu.deliveryfee.model.WeatherPhenomenon;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.delivery-fee")
public class DeliveryFeeProperties {

    private Map<City, Map<VehicleType, BigDecimal>> baseFees = new EnumMap<>(City.class);
    private Map<VehicleType, VehicleRuleProperties> vehicleRules = new EnumMap<>(VehicleType.class);

    /**
     * Returns base fees by city and vehicle type.
     */
    public Map<City, Map<VehicleType, BigDecimal>> getBaseFees() {
        return baseFees;
    }

    /**
     * Sets base fees by city and vehicle type.
     */
    public void setBaseFees(Map<City, Map<VehicleType, BigDecimal>> baseFees) {
        this.baseFees = baseFees;
    }

    /**
     * Returns additional weather-based rules by vehicle type.
     */
    public Map<VehicleType, VehicleRuleProperties> getVehicleRules() {
        return vehicleRules;
    }

    /**
     * Sets additional weather-based rules by vehicle type.
     */
    public void setVehicleRules(Map<VehicleType, VehicleRuleProperties> vehicleRules) {
        this.vehicleRules = vehicleRules;
    }

    public static class VehicleRuleProperties {
        private TemperatureRuleProperties temperature = new TemperatureRuleProperties();
        private WindRuleProperties wind = new WindRuleProperties();
        private PhenomenonRuleProperties phenomenon = new PhenomenonRuleProperties();

        /**
         * Returns temperature-based fee rules.
         */
        public TemperatureRuleProperties getTemperature() {
            return temperature;
        }

        /**
         * Sets temperature-based fee rules.
         */
        public void setTemperature(TemperatureRuleProperties temperature) {
            this.temperature = temperature;
        }

        /**
         * Returns wind-based fee rules.
         */
        public WindRuleProperties getWind() {
            return wind;
        }

        /**
         * Sets wind-based fee rules.
         */
        public void setWind(WindRuleProperties wind) {
            this.wind = wind;
        }

        /**
         * Returns weather phenomenon rules.
         */
        public PhenomenonRuleProperties getPhenomenon() {
            return phenomenon;
        }

        /**
         * Sets weather phenomenon rules.
         */
        public void setPhenomenon(PhenomenonRuleProperties phenomenon) {
            this.phenomenon = phenomenon;
        }
    }

    public static class TemperatureRuleProperties {
        private BigDecimal belowMinusTen = BigDecimal.ZERO;
        private BigDecimal betweenMinusTenAndZero = BigDecimal.ZERO;

        /**
         * Returns the surcharge applied below -10 degrees.
         */
        public BigDecimal getBelowMinusTen() {
            return belowMinusTen;
        }

        /**
         * Sets the surcharge applied below -10 degrees.
         */
        public void setBelowMinusTen(BigDecimal belowMinusTen) {
            this.belowMinusTen = belowMinusTen;
        }

        /**
         * Returns the surcharge applied from -10 to 0 degrees.
         */
        public BigDecimal getBetweenMinusTenAndZero() {
            return betweenMinusTenAndZero;
        }

        /**
         * Sets the surcharge applied from -10 to 0 degrees.
         */
        public void setBetweenMinusTenAndZero(BigDecimal betweenMinusTenAndZero) {
            this.betweenMinusTenAndZero = betweenMinusTenAndZero;
        }
    }

    public static class WindRuleProperties {
        private BigDecimal aboveTenUpToTwenty = BigDecimal.ZERO;
        private BigDecimal forbiddenAbove;

        /**
         * Returns the surcharge applied when wind speed is above 10 m/s and up to 20 m/s.
         */
        public BigDecimal getAboveTenUpToTwenty() {
            return aboveTenUpToTwenty;
        }

        /**
         * Sets the surcharge applied when wind speed is above 10 m/s and up to 20 m/s.
         */
        public void setAboveTenUpToTwenty(BigDecimal aboveTenUpToTwenty) {
            this.aboveTenUpToTwenty = aboveTenUpToTwenty;
        }

        /**
         * Returns the wind speed threshold above which usage is forbidden.
         */
        public BigDecimal getForbiddenAbove() {
            return forbiddenAbove;
        }

        /**
         * Sets the wind speed threshold above which usage is forbidden.
         */
        public void setForbiddenAbove(BigDecimal forbiddenAbove) {
            this.forbiddenAbove = forbiddenAbove;
        }
    }

    public static class PhenomenonRuleProperties {
        private BigDecimal snowOrSleet = BigDecimal.ZERO;
        private BigDecimal rain = BigDecimal.ZERO;
        private Set<WeatherPhenomenon> forbidden = new HashSet<>();

        /**
         * Returns the surcharge applied for snow or sleet.
         */
        public BigDecimal getSnowOrSleet() {
            return snowOrSleet;
        }

        /**
         * Sets the surcharge applied for snow or sleet.
         */
        public void setSnowOrSleet(BigDecimal snowOrSleet) {
            this.snowOrSleet = snowOrSleet;
        }

        /**
         * Returns the surcharge applied for rain.
         */
        public BigDecimal getRain() {
            return rain;
        }

        /**
         * Sets the surcharge applied for rain.
         */
        public void setRain(BigDecimal rain) {
            this.rain = rain;
        }

        /**
         * Returns the set of forbidden weather phenomena.
         */
        public Set<WeatherPhenomenon> getForbidden() {
            return forbidden;
        }

        /**
         * Sets the set of forbidden weather phenomena.
         */
        public void setForbidden(Set<WeatherPhenomenon> forbidden) {
            this.forbidden = forbidden;
        }
    }
}

