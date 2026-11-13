package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.ports.in.GetDeliveryFleetUseCase;
import com.pizzeria_api.ports.out.DeliveryDriverRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DeliveryFleetService implements GetDeliveryFleetUseCase {

    private final DeliveryDriverRepositoryPort deliveryDriverRepositoryPort;

    public DeliveryFleetService(DeliveryDriverRepositoryPort deliveryDriverRepositoryPort) {
        this.deliveryDriverRepositoryPort = deliveryDriverRepositoryPort;
    }

    @Override
    public List<DeliveryDriver> getAllDrivers() {
        return deliveryDriverRepositoryPort.findAll();
    }

    @Override
    public List<DeliveryDriver> getAvailableDrivers() {
        return deliveryDriverRepositoryPort.findAvailableDrivers();
    }
}
