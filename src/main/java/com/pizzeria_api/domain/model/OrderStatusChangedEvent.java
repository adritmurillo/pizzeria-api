package com.pizzeria_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        OrderStatus newStatus,
        LocalDateTime timestamp
) {
    public OrderStatusChangedEvent(UUID orderId, OrderStatus newStatus){
        this(orderId, newStatus, LocalDateTime.now());
    }
}
