package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;

import java.math.BigDecimal;
import java.util.UUID;

public record PizzaResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        PizzaSize size,
        PizzaCategory category,
        boolean available
) {
    public static PizzaResponse fromDomain(Pizza pizza){
        return new PizzaResponse(
                pizza.getId(),
                pizza.getName(),
                pizza.getDescription(),
                pizza.getPrice(),
                pizza.getSize(),
                pizza.getCategory(),
                pizza.isAvailable()
        );
    }
}
