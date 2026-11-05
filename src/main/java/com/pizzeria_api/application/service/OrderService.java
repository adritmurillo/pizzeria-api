package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService implements CreateOrderUseCase {
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
}
