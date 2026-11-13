package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.domain.model.DriverStatus;
import com.pizzeria_api.ports.out.DeliveryDriverRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDeliveryDriverRepository implements DeliveryDriverRepositoryPort {

    private final Map<UUID, DeliveryDriver> storage = new ConcurrentHashMap<>();

    @Override
    public DeliveryDriver save(DeliveryDriver driver) {
        storage.put(driver.getId(), driver);
        return driver;
    }

    @Override
    public Optional<DeliveryDriver> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<DeliveryDriver> findAvailableDrivers() {
        return storage.values().stream()
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                .toList();
    }

    @Override
    public List<DeliveryDriver> findAll() {
        return List.copyOf(storage.values());
    }
}
