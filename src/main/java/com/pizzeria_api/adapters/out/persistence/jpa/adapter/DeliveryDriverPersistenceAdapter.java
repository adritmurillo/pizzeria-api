package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.DeliveryDriverJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataDeliveryDriverRepository;
import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.domain.model.DriverStatus;
import com.pizzeria_api.ports.out.DeliveryDriverRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class DeliveryDriverPersistenceAdapter implements DeliveryDriverRepositoryPort {

    private final SpringDataDeliveryDriverRepository springDataDeliveryDriverRepository;

    public DeliveryDriverPersistenceAdapter(SpringDataDeliveryDriverRepository springDataDeliveryDriverRepository) {
        this.springDataDeliveryDriverRepository = springDataDeliveryDriverRepository;
    }

    @Override
    public DeliveryDriver save(DeliveryDriver driver) {
        Optional<DeliveryDriverJpaEntity> existingOpt = springDataDeliveryDriverRepository.findById(driver.getId());

        DeliveryDriverJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            entityToSave = existingOpt.get();
            entityToSave.setName(driver.getName());
            entityToSave.setPhone(driver.getPhone());
            entityToSave.setVehicleType(driver.getVehicleType());
            entityToSave.setMaxCapacityPizzas(driver.getMaxCapacityPizzas());
            entityToSave.setStatus(driver.getStatus());
            entityToSave.setCurrentOrderId(driver.getCurrentOrderId());
            entityToSave.setCompletedDeliveries(driver.getCompletedDeliveries());
        } else {
            entityToSave = toEntity(driver);
        }

        DeliveryDriverJpaEntity savedEntity = springDataDeliveryDriverRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<DeliveryDriver> findById(UUID id) {
        return springDataDeliveryDriverRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<DeliveryDriver> findAvailableDrivers() {
        return springDataDeliveryDriverRepository
                .findByStatusOrderByCompletedDeliveriesAsc(DriverStatus.AVAILABLE)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<DeliveryDriver> findAll() {
        return springDataDeliveryDriverRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private DeliveryDriverJpaEntity toEntity(DeliveryDriver domain) {
        return new DeliveryDriverJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getPhone(),
                domain.getVehicleType(),
                domain.getMaxCapacityPizzas(),
                domain.getStatus(),
                domain.getCurrentOrderId(),
                domain.getCompletedDeliveries()
        );
    }

    private DeliveryDriver toDomain(DeliveryDriverJpaEntity entity) {
        return new DeliveryDriver(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getVehicleType(),
                entity.getMaxCapacityPizzas(),
                entity.getStatus(),
                entity.getCurrentOrderId(),
                entity.getCompletedDeliveries()
        );
    }
}
