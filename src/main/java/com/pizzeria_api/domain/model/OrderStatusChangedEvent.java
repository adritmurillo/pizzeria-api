package com.pizzeria_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        OrderStatus newStatus,
        int estimatedWaitMinutes,
        LocalDateTime estimatedReadyAt,
        UUID assignedDriverId,
        String assignedDriverName,
        LocalDateTime timestamp
) {
    public OrderStatusChangedEvent(UUID orderId,
                                   OrderStatus newStatus,
                                   int estimatedWaitMinutes,
                                   LocalDateTime estimatedReadyAt,
                                   UUID assignedDriverId,
                                   String assignedDriverName) {
        this(orderId, newStatus, estimatedWaitMinutes, estimatedReadyAt, assignedDriverId, assignedDriverName, LocalDateTime.now());
    }

    public OrderStatusChangedEvent(UUID orderId, OrderStatus newStatus, int estimatedWaitMinutes, LocalDateTime estimatedReadyAt) {
        this(orderId, newStatus, estimatedWaitMinutes, estimatedReadyAt, null, null, LocalDateTime.now());
    }

    public OrderStatusChangedEvent(UUID orderId, OrderStatus newStatus) {
        this(orderId, newStatus, 0, null, null, null, LocalDateTime.now());
    }
}
