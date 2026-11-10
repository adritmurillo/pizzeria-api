package com.pizzeria_api.ports.out;

import com.pizzeria_api.domain.model.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepositoryPort {
    Ingredient save(Ingredient ingredient);
    Optional<Ingredient> findById(UUID id);
    List<Ingredient> findAll();
}
