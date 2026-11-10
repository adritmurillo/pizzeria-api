package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.exception.OrderNotFoundException;
import com.pizzeria_api.domain.exception.PizzaNotFoundException;
import com.pizzeria_api.domain.model.*;
import com.pizzeria_api.domain.service.KitchenQueueEstimator;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.in.GetOrderUseCase;
import com.pizzeria_api.ports.in.GetOrdersUseCase;
import com.pizzeria_api.ports.in.UpdateOrderStatusUseCase;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class OrderService implements CreateOrderUseCase,
        GetOrderUseCase, UpdateOrderStatusUseCase, GetOrdersUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PizzaRepositoryPort pizzaRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;
    private final KitchenQueueEstimator kitchenQueueEstimator = new KitchenQueueEstimator();
    private final Map<String, Order> idempotencyRecords = new ConcurrentHashMap<>();

    public OrderService(OrderRepositoryPort orderRepositoryPort,
                        PizzaRepositoryPort pizzaRepositoryPort,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.pizzaRepositoryPort = pizzaRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order createOrder(CreateOrderCommand command, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Order existingOrder = idempotencyRecords.get(idempotencyKey);
            if (existingOrder != null) {
                return existingOrder;
            }
        }

        List<OrderItem> domainItems = new ArrayList<>();
        for (OrderItemCommand itemCommand : command.items()) {
            Pizza pizza = pizzaRepositoryPort.findById(itemCommand.pizzaId())
                    .orElseThrow(() -> new PizzaNotFoundException("Pizza not found with id: " + itemCommand.pizzaId()));

            if (!pizza.isAvailable()) {
                throw new InvalidOrderException("Pizza '" + pizza.getName() + "' is currently unavailable");
            }

            pizza.deductStock(itemCommand.quantity());
            pizzaRepositoryPort.save(pizza);

            domainItems.add(new OrderItem(
                    pizza.getId(),
                    pizza.getName(),
                    itemCommand.quantity(),
                    pizza.getPrice()
            ));
        }

        Order orderToSave = new Order(
                null,
                command.customerName(),
                command.deliveryAddress(),
                domainItems
        );

        int pizzasAhead = orderRepositoryPort.countActivePizzasInQueue();
        int estimatedMinutes = kitchenQueueEstimator.calculateEstimatedWaitMinutes(
                pizzasAhead,
                orderToSave.getTotalPizzaCount()
        );
        orderToSave.assignEstimatedWaitTime(estimatedMinutes);

        Order savedOrder = orderRepositoryPort.save(orderToSave);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyRecords.put(idempotencyKey, savedOrder);
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderRepositoryPort.findById(id).orElseThrow(() -> new
                OrderNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Order updateStatus(UUID id, OrderStatus newStatus) {
        Order order = getOrderById(id);
        switch (newStatus) {
            case PREPARING -> order.markAsPreparing();
            case READY -> order.markAsReady();
            case DELIVERED -> order.markAsDelivered();
            case CANCELLED -> {
                order.cancel();
                for (OrderItem item : order.getItems()) {
                    pizzaRepositoryPort.findById(item.getPizzaId()).ifPresent(p -> {
                        p.restoreStock(item.getQuantity());
                        pizzaRepositoryPort.save(p);
                    });
                }
            }
            case PENDING -> throw new InvalidOrderException("Cannot revert order back to PENDING");
        }

        Order savedOrder = orderRepositoryPort.save(order);

        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getEstimatedWaitMinutes(),
                savedOrder.getEstimatedReadyAt()
        ));

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Order> getOrders(OrderStatus status, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 10 : Math.min(size, 50);

        return orderRepositoryPort.findOrders(status, safePage, safeSize);
    }
}
