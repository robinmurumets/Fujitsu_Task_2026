package com.fujitsu.deliveryfee.service.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EstonianEnvironmentAgencyXmlParserTest {

    private final EstonianEnvironmentAgencyXmlParser parser = new EstonianEnvironmentAgencyXmlParser();

    @Test
    void shouldParseOnlyRequiredStationsFromObservationsXml() {
        String xml = """
                <observations timestamp="1774865700">
                  <station>
                    <name>Tallinn-Harku</name>
                    <wmocode>26038</wmocode>
                    <airtemperature>-1.5</airtemperature>
                    <windspeed>12.4</windspeed>
                    <phenomenon>Light snow shower</phenomenon>
                  </station>
                  <station>
                    <name>Tartu-Tõravere</name>
                    <wmocode>26242</wmocode>
                    <airtemperature>-4.0</airtemperature>
                    <windspeed>4.7</windspeed>
                    <phenomenon>Clear</phenomenon>
                  </station>
                  <station>
                    <name>Pärnu</name>
                    <wmocode>41803</wmocode>
                    <airtemperature>2.0</airtemperature>
                    <windspeed>8.1</windspeed>
                    <phenomenon>Light rain</phenomenon>
                  </station>
                  <station>
                    <name>Kuressaare</name>
                    <wmocode>12345</wmocode>
                    <airtemperature>5.0</airtemperature>
                    <windspeed>6.0</windspeed>
                    <phenomenon>Clear</phenomenon>
                  </station>
                </observations>
                """;

        List<WeatherData> observations = parser.parse(xml);

        assertEquals(3, observations.size());
        assertEquals(City.TALLINN, observations.get(0).getCity());
        assertEquals("26038", observations.get(0).getWmoCode());
        assertEquals("Light snow shower", observations.get(0).getWeatherPhenomenon());
        assertEquals(OffsetDateTime.parse("2026-03-30T10:15:00Z"), observations.get(0).getObservedAt());
        assertEquals(City.TARTU, observations.get(1).getCity());
        assertEquals(City.PARNU, observations.get(2).getCity());
    }
}
