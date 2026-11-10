package com.pizzeria_api.adapters.out.persistence.jpa.repository;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderJpaEntity;
import com.pizzeria_api.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataOrderRepository extends
        JpaRepository<OrderJpaEntity, UUID> {
    Page<OrderJpaEntity> findByStatusOrderByCreatedAtAsc(OrderStatus status, Pageable pageable);
    Page<OrderJpaEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM OrderItemJpaEntity i WHERE i.order.status IN (com.pizzeria_api.domain.model.OrderStatus.PENDING, com.pizzeria_api.domain.model.OrderStatus.PREPARING)")
    int countActivePizzasInQueue();
}
