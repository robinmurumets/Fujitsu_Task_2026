package com.fujitsu.deliveryfee.service.weather;

import com.fujitsu.deliveryfee.config.WeatherApiProperties;
import com.fujitsu.deliveryfee.exception.WeatherImportException;
import com.fujitsu.deliveryfee.model.WeatherData;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EstonianEnvironmentAgencyWeatherClient implements WeatherClient {

    private final RestClient restClient;
    private final WeatherApiProperties weatherApiProperties;
    private final EstonianEnvironmentAgencyXmlParser xmlParser;

    /**
     * Creates the client with the HTTP client, configuration and XML parser.
     */
    public EstonianEnvironmentAgencyWeatherClient(
            RestClient restClient,
            WeatherApiProperties weatherApiProperties,
            EstonianEnvironmentAgencyXmlParser xmlParser
    ) {
        this.restClient = restClient;
        this.weatherApiProperties = weatherApiProperties;
        this.xmlParser = xmlParser;
    }

    /**
     * Downloads the latest XML observation feed and converts it into weather entities.
     */
    @Override
    public List<WeatherData> fetchCurrentWeather() {
        try {
            String xml = restClient.get()
                    .uri(weatherApiProperties.getObservationsUrl())
                    .retrieve()
                    .body(String.class);

            if (xml == null || xml.isBlank()) {
                throw new WeatherImportException("Environment Agency observations response was empty", null);
            }

            return xmlParser.parse(xml);
        } catch (WeatherImportException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new WeatherImportException("Failed to import weather from Estonian Environment Agency XML feed", exception);
        }
    }
}
