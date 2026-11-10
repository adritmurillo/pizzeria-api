package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.Ingredient;
import com.pizzeria_api.ports.out.IngredientRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryIngredientRepository implements IngredientRepositoryPort {

    private final Map<UUID, Ingredient> ingredientDatabase = new ConcurrentHashMap<>();

    @Override
    public Ingredient save(Ingredient ingredient) {
        ingredientDatabase.put(ingredient.getId(), ingredient);
        return ingredient;
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        return Optional.ofNullable(ingredientDatabase.get(id));
    }

    @Override
    public List<Ingredient> findAll() {
        return new ArrayList<>(ingredientDatabase.values());
    }
}
