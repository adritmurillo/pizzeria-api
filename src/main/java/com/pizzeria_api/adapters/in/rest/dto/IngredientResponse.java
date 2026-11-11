package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.Ingredient;
import com.pizzeria_api.domain.model.IngredientUnit;

import java.math.BigDecimal;
import java.util.UUID;

public record IngredientResponse(
        UUID id,
        String name,
        IngredientUnit unit,
        int stock,
        BigDecimal costPerUnit
) {
    public static IngredientResponse fromDomain(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getUnit(),
                ingredient.getStock(),
                ingredient.getCostPerUnit()
        );
    }
}
