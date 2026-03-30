package com.fujitsu.deliveryfee.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fujitsu.deliveryfee.dto.DeliveryFeeResponse;
import com.fujitsu.deliveryfee.exception.FeeCalculationException;
import com.fujitsu.deliveryfee.exception.GlobalExceptionHandler;
import com.fujitsu.deliveryfee.model.City;
import com.fujitsu.deliveryfee.model.VehicleType;
import com.fujitsu.deliveryfee.service.DeliveryFeeService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryFeeController.class)
@Import(GlobalExceptionHandler.class)
class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeliveryFeeService deliveryFeeService;

    @Test
    void shouldReturnDeliveryFeeResponse() throws Exception {
        when(deliveryFeeService.calculateFee(any())).thenReturn(new DeliveryFeeResponse(
                City.TALLINN,
                VehicleType.CAR,
                OffsetDateTime.parse("2026-03-30T10:15:00Z"),
                new BigDecimal("4.00")
        ));

        mockMvc.perform(get("/api/v1/delivery-fees")
                        .param("city", "TALLINN")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("TALLINN"))
                .andExpect(jsonPath("$.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.fee").value(4.00));
    }

    @Test
    void shouldReturnStructuredErrorWhenFeeCalculationFails() throws Exception {
        when(deliveryFeeService.calculateFee(any()))
                .thenThrow(new FeeCalculationException("Usage of selected vehicle type is forbidden"));

        mockMvc.perform(get("/api/v1/delivery-fees")
                        .param("city", "TALLINN")
                        .param("vehicleType", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Usage of selected vehicle type is forbidden"))
                .andExpect(jsonPath("$.details").isArray());
    }
}
