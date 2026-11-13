package com.pizzeria_api.adapters.out.persistence.jpa.repository;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.DeliveryDriverJpaEntity;
import com.pizzeria_api.domain.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDeliveryDriverRepository extends JpaRepository<DeliveryDriverJpaEntity, UUID> {
    List<DeliveryDriverJpaEntity> findByStatusOrderByCompletedDeliveriesAsc(DriverStatus status);
}
