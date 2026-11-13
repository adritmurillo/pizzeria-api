package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Order;

import java.util.UUID;

public interface DispatchOrderUseCase {
    Order dispatchOrder(UUID orderId);
}
