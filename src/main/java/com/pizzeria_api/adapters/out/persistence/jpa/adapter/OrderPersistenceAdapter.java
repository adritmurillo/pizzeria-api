package com.pizzeria_api.adapters.out.persistence.jpa.adapter;


import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataOrderRepository;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class OrderPersistenceAdapter implements OrderRepositoryPort {
    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository springDataOrderRepository){
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toEntity(order);
        OrderJpaEntity savedEntity = springDataOrderRepository.save(entity);
        return toDomain(savedEntity, order.getItems());
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataOrderRepository.findById(id).map(entity -> toDomain(entity, Collections.emptyList()));
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(entity -> toDomain(entity, Collections.emptyList())).toList();
    }

    private OrderJpaEntity toEntity(Order domain) {
        return new OrderJpaEntity(
                domain.getId(),
                domain.getCustomerName(),
                domain.getDeliveryAddress(),
                domain.getTotalAmount(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }

    private Order toDomain(OrderJpaEntity entity, List<OrderItem>
            items) {
        List<OrderItem> safeItems = (items != null && !items.isEmpty())
                ? items
                : List.of(new OrderItem(UUID.randomUUID(), "Order Item", 1, entity.getTotalAmount()));

        return new Order(
                entity.getId(),
                entity.getCustomerName(),
                entity.getDeliveryAddress(),
                safeItems,
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

}
