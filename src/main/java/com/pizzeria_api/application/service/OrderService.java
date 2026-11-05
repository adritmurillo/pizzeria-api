package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.exception.OrderNotFoundException;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.in.GetOrderUseCase;
import com.pizzeria_api.ports.in.UpdateOrderStatusUseCase;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    private final Map<String, Order> idempotencyRecords = new ConcurrentHashMap<>();

    public OrderService(OrderRepositoryPort orderRepositoryPort){
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order createOrder(Order order, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()){
            Order existingOrder = idempotencyRecords.get(idempotencyKey);
            if (existingOrder != null) {
                return existingOrder;
            }
        }

        Order savedOrder = orderRepositoryPort.save(order);
        if (idempotencyKey != null && !idempotencyKey.isBlank()){
            idempotencyRecords.put(idempotencyKey, savedOrder);
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderRepositoryPort.findById(id).orElseThrow(()-> new OrderNotFoundException(
                "Order not found with id: "+id));
    }

    @Override
    public Order updateStatus(UUID id, OrderStatus newStatus) {
        Order order = getOrderById(id);
        switch (newStatus){
            case PREPARING -> order.markAsPreparing();
            case READY -> order.markAsReady();
            case DELIVERED -> order.markAsDelivered();
            case PENDING -> throw new InvalidOrderException("Cannot revert order back to PENDING");
        }

        return orderRepositoryPort.save(order);
    }
}
