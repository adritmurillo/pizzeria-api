package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidPizzaException;

import java.util.Objects;
import java.util.UUID;

public record RecipeItem(
        UUID ingredientId,
        String ingredientName,
        int quantityRequired,
        IngredientUnit unit
) {
    public RecipeItem {
        if (ingredientId == null) {
            throw new InvalidPizzaException("Ingredient ID cannot be null");
        }
        if (ingredientName == null || ingredientName.trim().isBlank()) {
            throw new InvalidPizzaException("Ingredient name cannot be empty");
        }
        if (quantityRequired <= 0) {
            throw new InvalidPizzaException("Quantity required must be greater than zero");
        }
        unit = Objects.requireNonNullElse(unit, IngredientUnit.UNITS);
    }
}
