package com.fujitsu.deliveryfee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "weather_data",
        indexes = {
                @Index(name = "idx_weather_city_observed_at", columnList = "city, observed_at")
        }
)
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private City city;

    @Column(name = "observed_at", nullable = false)
    private OffsetDateTime observedAt;

    @Column(name = "station_name", nullable = false, length = 80)
    private String stationName;

    @Column(name = "wmo_code", nullable = false, length = 20)
    private String wmoCode;

    @Column(nullable = false)
    private double airTemperature;

    @Column(nullable = false)
    private double windSpeed;

    @Column(name = "weather_phenomenon", nullable = false, length = 120)
    private String weatherPhenomenon;

    @Column(nullable = false, length = 80)
    private String source;
}
