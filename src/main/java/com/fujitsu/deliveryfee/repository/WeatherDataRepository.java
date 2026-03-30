package com.fujitsu.deliveryfee.repository;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    Optional<WeatherData> findFirstByCityOrderByObservedAtDesc(City city);

    Optional<WeatherData> findFirstByCityAndObservedAtLessThanEqualOrderByObservedAtDesc(City city, OffsetDateTime observedAt);
}
