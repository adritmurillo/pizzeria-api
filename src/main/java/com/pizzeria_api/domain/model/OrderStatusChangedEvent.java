package com.pizzeria_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        OrderStatus newStatus,
        int estimatedWaitMinutes,
        LocalDateTime estimatedReadyAt,
        LocalDateTime timestamp
) {
    public OrderStatusChangedEvent(UUID orderId, OrderStatus newStatus, int estimatedWaitMinutes, LocalDateTime estimatedReadyAt) {
        this(orderId, newStatus, estimatedWaitMinutes, estimatedReadyAt, LocalDateTime.now());
    }

    public OrderStatusChangedEvent(UUID orderId, OrderStatus newStatus) {
        this(orderId, newStatus, 0, null, LocalDateTime.now());
    }
}
