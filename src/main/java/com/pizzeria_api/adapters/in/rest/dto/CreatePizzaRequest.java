package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePizzaRequest(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 3, message = "Name must have at least 3 characters")
                String name,

        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Size is required")
        PizzaSize size,

        @NotNull(message = "Category is required")
        PizzaCategory category
) {
}