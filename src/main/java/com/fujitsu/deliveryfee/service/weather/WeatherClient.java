package com.fujitsu.deliveryfee.service.weather;

import com.fujitsu.deliveryfee.model.WeatherData;
import java.util.List;

public interface WeatherClient {

    /**
     * Fetches the current weather observations used by the application.
     */
    List<WeatherData> fetchCurrentWeather();
}

