package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.adapters.in.rest.dto.IngredientResponse;
import com.pizzeria_api.ports.in.GetIngredientsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientRestController {

    private final GetIngredientsUseCase getIngredientsUseCase;

    public IngredientRestController(GetIngredientsUseCase getIngredientsUseCase) {
        this.getIngredientsUseCase = getIngredientsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getIngredients() {
        List<IngredientResponse> ingredients = getIngredientsUseCase.getIngredients().stream()
                .map(IngredientResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(ingredients);
    }
}
