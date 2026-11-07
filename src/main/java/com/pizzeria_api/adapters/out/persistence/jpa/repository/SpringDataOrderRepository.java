package com.pizzeria_api.adapters.out.persistence.jpa.repository;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderJpaEntity;
import com.pizzeria_api.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataOrderRepository extends
        JpaRepository<OrderJpaEntity, UUID> {
    Page<OrderJpaEntity> findByStatusOrderByCreatedAtAsc(OrderStatus status, Pageable pageable);
    Page<OrderJpaEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
}