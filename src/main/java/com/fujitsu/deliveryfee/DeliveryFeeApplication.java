package com.fujitsu.deliveryfee;

import com.fujitsu.deliveryfee.config.DeliveryFeeProperties;
import com.fujitsu.deliveryfee.config.WeatherApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({DeliveryFeeProperties.class, WeatherApiProperties.class})
public class DeliveryFeeApplication {

    /**
     * Starts the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(DeliveryFeeApplication.class, args);
    }
}

