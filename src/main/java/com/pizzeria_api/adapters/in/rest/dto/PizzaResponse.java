package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.IngredientUnit;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;
import com.pizzeria_api.domain.model.RecipeItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PizzaResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        PizzaSize size,
        PizzaCategory category,
        boolean available,
        int stock,
        List<RecipeItemResponse> recipe
) {
    public record RecipeItemResponse(
            UUID ingredientId,
            String ingredientName,
            int quantityRequired,
            IngredientUnit unit
    ) {
        public static RecipeItemResponse fromDomain(RecipeItem item) {
            return new RecipeItemResponse(
                    item.ingredientId(),
                    item.ingredientName(),
                    item.quantityRequired(),
                    item.unit()
            );
        }
    }

    public static PizzaResponse fromDomain(Pizza pizza){
        List<RecipeItemResponse> recipeResponses = (pizza.getRecipe() == null) ? List.of() :
                pizza.getRecipe().stream()
                        .map(RecipeItemResponse::fromDomain)
                        .toList();

        return new PizzaResponse(
                pizza.getId(),
                pizza.getName(),
                pizza.getDescription(),
                pizza.getPrice(),
                pizza.getSize(),
                pizza.getCategory(),
                pizza.isAvailable(),
                pizza.getStock(),
                recipeResponses
        );
    }
}
