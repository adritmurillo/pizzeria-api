package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Ingredient;

import java.util.List;

public interface GetIngredientsUseCase {
    List<Ingredient> getIngredients();
}
