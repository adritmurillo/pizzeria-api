package com.pizzeria_api.domain;

import com.pizzeria_api.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaRecipeTest {

    @Test
    @DisplayName("Should create pizza with BOM recipe successfully")
    void shouldCreatePizzaWithRecipe() {
        RecipeItem dough = new RecipeItem(UUID.randomUUID(), "Masa Clásica", 1, IngredientUnit.UNITS);
        RecipeItem cheese = new RecipeItem(UUID.randomUUID(), "Queso Mozzarella", 200, IngredientUnit.GRAMS);
        RecipeItem pepperoni = new RecipeItem(UUID.randomUUID(), "Pepperoni", 80, IngredientUnit.GRAMS);

        Pizza pizza = new Pizza(
                UUID.randomUUID(),
                "Pepperoni Clásica",
                "Con masa fresca y pepperoni",
                new BigDecimal("16.00"),
                PizzaSize.LARGE,
                PizzaCategory.CLASSIC,
                true,
                50,
                List.of(dough, cheese, pepperoni)
        );

        assertTrue(pizza.hasRecipe());
        assertEquals(3, pizza.getRecipe().size());
        assertEquals("Masa Clásica", pizza.getRecipe().get(0).ingredientName());
        assertEquals(200, pizza.getRecipe().get(1).quantityRequired());
    }
}
