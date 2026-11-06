package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderItemJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataOrderRepository;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository
                                           springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        Optional<OrderJpaEntity> existingOpt =
                springDataOrderRepository.findById(order.getId());

        OrderJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            // Actualización de orden existente (ej. cambio de estado a PREPARING/READY)
            entityToSave = existingOpt.get();
            entityToSave.setStatus(order.getStatus());
            entityToSave.setTotalAmount(order.getTotalAmount());
            entityToSave.setCustomerName(order.getCustomerName());
            entityToSave.setDeliveryAddress(order.getDeliveryAddress())
            ;
        } else {
            // Creación de una nueva comanda con todos sus ítems
            entityToSave = toEntity(order);
        }

        OrderJpaEntity savedEntity =
                springDataOrderRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return
                springDataOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private OrderJpaEntity toEntity(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity(
                domain.getId(),
                domain.getCustomerName(),
                domain.getDeliveryAddress(),
                domain.getTotalAmount(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        if (domain.getItems() != null) {
            for (OrderItem item : domain.getItems()) {
                OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                        UUID.randomUUID(),
                        entity,
                        item.getPizzaId(),
                        item.getPizzaName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.calculateSubtotal()
                );
                entity.addItem(itemEntity);
            }
        }

        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
                .map(itemEntity -> new OrderItem(
                        itemEntity.getPizzaId(),
                        itemEntity.getPizzaName(),
                        itemEntity.getQuantity(),
                        itemEntity.getUnitPrice()
                ))
                .toList();

        return new Order(
                entity.getId(),
                entity.getCustomerName(),
                entity.getDeliveryAddress(),
                domainItems,
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}