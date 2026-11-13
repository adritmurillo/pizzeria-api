package com.pizzeria_api.ports.out;

import com.pizzeria_api.domain.model.DeliveryDriver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryDriverRepositoryPort {
    DeliveryDriver save(DeliveryDriver driver);
    Optional<DeliveryDriver> findById(UUID id);
    List<DeliveryDriver> findAvailableDrivers();
    List<DeliveryDriver> findAll();
}
