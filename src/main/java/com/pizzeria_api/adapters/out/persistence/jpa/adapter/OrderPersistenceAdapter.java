package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.entity.OrderItemJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataOrderRepository;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.domain.model.PagedResult;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        Optional<OrderJpaEntity> existingOpt = springDataOrderRepository.findById(order.getId());

        OrderJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            entityToSave = existingOpt.get();
            entityToSave.setStatus(order.getStatus());
            entityToSave.setTotalAmount(order.getTotalAmount());
            entityToSave.setCustomerName(order.getCustomerName());
            entityToSave.setDeliveryAddress(order.getDeliveryAddress());
            entityToSave.setEstimatedWaitMinutes(order.getEstimatedWaitMinutes());
            entityToSave.setEstimatedReadyAt(order.getEstimatedReadyAt());
        } else {
            entityToSave = toEntity(order);
        }

        OrderJpaEntity savedEntity = springDataOrderRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PagedResult<Order> findOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderJpaEntity> pageResult = (status != null)
                ? springDataOrderRepository.findByStatusOrderByCreatedAtAsc(status, pageable)
                : springDataOrderRepository.findAllByOrderByCreatedAtAsc(pageable);

        List<Order> domainOrders = pageResult.getContent().stream()
                .map(this::toDomain).toList();

        return new PagedResult<>(
                domainOrders,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }

    @Override
    public int countActivePizzasInQueue() {
        return springDataOrderRepository.countActivePizzasInQueue();
    }

    private OrderJpaEntity toEntity(Order domain) {
        OrderJpaEntity entity = new OrderJpaEntity(
                domain.getId(),
                domain.getCustomerName(),
                domain.getDeliveryAddress(),
                domain.getTotalAmount(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getEstimatedWaitMinutes(),
                domain.getEstimatedReadyAt()
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
        List<OrderItem> domainItems = (entity.getItems() == null || entity.getItems().isEmpty())
                ? List.of(new OrderItem(UUID.randomUUID(), "Legacy Order Item", 1, entity.getTotalAmount()))
                : entity.getItems().stream()
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
                entity.getCreatedAt(),
                entity.getEstimatedWaitMinutes(),
                entity.getEstimatedReadyAt()
        );
    }
}
