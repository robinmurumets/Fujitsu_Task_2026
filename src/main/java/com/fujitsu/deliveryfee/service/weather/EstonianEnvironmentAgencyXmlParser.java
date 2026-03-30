package com.fujitsu.deliveryfee.service.weather;

import com.fujitsu.deliveryfee.exception.WeatherImportException;
import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.WeatherData;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Component
public class EstonianEnvironmentAgencyXmlParser {

    private static final String SOURCE = "ESTONIAN_ENVIRONMENT_AGENCY_XML";

    private static final Map<String, StationMapping> SUPPORTED_STATIONS = Map.of(
            "Tallinn-Harku", new StationMapping(City.TALLINN),
            "Tartu-Toravere", new StationMapping(City.TARTU),
            "Tartu-T\u00f5ravere", new StationMapping(City.TARTU),
            "P\u00e4rnu", new StationMapping(City.PARNU),
            "Parnu", new StationMapping(City.PARNU)
    );

    /**
     * Parses the Environment Agency observations XML and returns only the stations required by the task.
     */
    public List<WeatherData> parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Element root = document.getDocumentElement();
            OffsetDateTime observedAt = resolveObservedAt(root);

            NodeList stations = root.getElementsByTagName("station");
            List<WeatherData> weatherData = new ArrayList<>();
            for (int i = 0; i < stations.getLength(); i++) {
                Element stationElement = (Element) stations.item(i);
                String stationName = childText(stationElement, "name");
                StationMapping stationMapping = SUPPORTED_STATIONS.get(stationName);
                if (stationMapping == null) {
                    continue;
                }

                WeatherData observation = new WeatherData();
                observation.setCity(stationMapping.city());
                observation.setStationName(stationName.trim());
                observation.setWmoCode(requiredChildText(stationElement, "wmocode", stationName));
                observation.setAirTemperature(parseDouble(stationElement, "airtemperature", stationName));
                observation.setWindSpeed(parseDouble(stationElement, "windspeed", stationName));
                observation.setWeatherPhenomenon(defaultPhenomenon(childText(stationElement, "phenomenon")));
                observation.setObservedAt(observedAt);
                observation.setSource(SOURCE);
                weatherData.add(observation);
            }

            return weatherData;
        } catch (WeatherImportException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new WeatherImportException("Failed to parse Estonian Environment Agency XML observations", exception);
        }
    }

    private OffsetDateTime resolveObservedAt(Element root) {
        String timestamp = root.getAttribute("timestamp");
        if (timestamp == null || timestamp.isBlank()) {
            throw new WeatherImportException("Observations XML is missing the root timestamp attribute", null);
        }
        return Instant.ofEpochSecond(Long.parseLong(timestamp)).atOffset(ZoneOffset.UTC);
    }

    private String requiredChildText(Element element, String tagName, String stationName) {
        String text = childText(element, tagName);
        if (text == null || text.isBlank()) {
            throw new WeatherImportException(
                    "Observations XML is missing <%s> for station %s".formatted(tagName, stationName),
                    null
            );
        }
        return text.trim();
    }

    private double parseDouble(Element element, String tagName, String stationName) {
        String text = requiredChildText(element, tagName, stationName);
        return Double.parseDouble(text.trim());
    }

    private String childText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0) == null) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }

    private String defaultPhenomenon(String phenomenon) {
        if (phenomenon == null || phenomenon.isBlank()) {
            return "Clear";
        }
        return phenomenon.trim();
    }

    private record StationMapping(City city) {
    }
}
