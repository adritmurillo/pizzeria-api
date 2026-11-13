package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.domain.model.DriverStatus;
import com.pizzeria_api.domain.model.VehicleType;

import java.util.UUID;

public record DeliveryDriverResponse(
        UUID id,
        String name,
        String phone,
        VehicleType vehicleType,
        int maxCapacityPizzas,
        DriverStatus status,
        UUID currentOrderId,
        int completedDeliveries
) {
    public static DeliveryDriverResponse fromDomain(DeliveryDriver driver) {
        return new DeliveryDriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getPhone(),
                driver.getVehicleType(),
                driver.getMaxCapacityPizzas(),
                driver.getStatus(),
                driver.getCurrentOrderId(),
                driver.getCompletedDeliveries()
        );
    }
}
