package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.Ingredient;
import com.pizzeria_api.ports.in.GetIngredientsUseCase;
import com.pizzeria_api.ports.out.IngredientRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class IngredientService implements GetIngredientsUseCase {

    private final IngredientRepositoryPort ingredientRepositoryPort;

    public IngredientService(IngredientRepositoryPort ingredientRepositoryPort) {
        this.ingredientRepositoryPort = ingredientRepositoryPort;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredientRepositoryPort.findAll();
    }
}
