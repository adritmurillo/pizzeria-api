package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
