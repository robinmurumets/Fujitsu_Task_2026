package com.fujitsu.deliveryfee.controller;

import com.fujitsu.deliveryfee.dto.DeliveryFeeRequest;
import com.fujitsu.deliveryfee.dto.DeliveryFeeResponse;
import com.fujitsu.deliveryfee.service.DeliveryFeeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery-fees")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    /**
     * Creates the controller with the delivery fee service.
     */
    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Returns the delivery fee for the requested city and vehicle type.
     */
    @GetMapping
    public DeliveryFeeResponse calculate(@Valid DeliveryFeeRequest request) {
        return deliveryFeeService.calculateFee(request);
    }
}
