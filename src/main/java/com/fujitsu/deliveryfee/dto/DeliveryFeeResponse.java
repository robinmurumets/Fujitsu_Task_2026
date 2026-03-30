package com.fujitsu.deliveryfee.dto;

import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record DeliveryFeeResponse(
        City city,
        VehicleType vehicleType,
        OffsetDateTime weatherObservedAt,
        BigDecimal fee
) {
}

