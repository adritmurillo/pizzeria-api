package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    Order updateStatus(UUID id, OrderStatus newStatus);
}
