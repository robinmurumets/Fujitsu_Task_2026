package com.fujitsu.deliveryfee.dto;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class DeliveryFeeRequest {

    @NotNull(message = "must be provided")
    private City city;

    @NotNull(message = "must be provided")
    private VehicleType vehicleType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime at;

    /**
     * Returns the requested delivery city.
     */
    public City getCity() {
        return city;
    }

    /**
     * Sets the requested delivery city.
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * Returns the requested vehicle type.
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Sets the requested vehicle type.
     */
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    /**
     * Returns the optional historical timestamp.
     */
    public OffsetDateTime getAt() {
        return at;
    }

    /**
     * Sets the optional historical timestamp.
     */
    public void setAt(OffsetDateTime at) {
        this.at = at;
    }
}
