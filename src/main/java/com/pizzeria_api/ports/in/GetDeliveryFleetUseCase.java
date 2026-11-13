package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.DeliveryDriver;

import java.util.List;

public interface GetDeliveryFleetUseCase {
    List<DeliveryDriver> getAllDrivers();
    List<DeliveryDriver> getAvailableDrivers();
}
