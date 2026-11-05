package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(Order order, String idempotencyKey);
}