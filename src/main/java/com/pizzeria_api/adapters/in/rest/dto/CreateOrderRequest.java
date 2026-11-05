package com.pizzeria_api.adapters.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Delivery address is required")
        String deliveryAddress,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "Pizza ID is required")
            UUID pizzaId,

            String pizzaName,

            @Positive(message = "Quantity must be at least 1")
            int quantity,

            @NotNull(message = "Unit price is required")
            @Positive(message = "Unit price must be greater than zero")
            BigDecimal unitPrice
    ) {}
}