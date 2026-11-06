package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command, String idempotencyKey);

    record CreateOrderCommand(
            String customerName,
            String deliveryAddress,
            List<OrderItemCommand> items
    ){}

    record OrderItemCommand(
            UUID pizzaId,
            int quantity
    ){}
}