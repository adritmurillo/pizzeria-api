package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InsufficientStockException;
import com.pizzeria_api.domain.exception.InvalidPizzaException;
import com.pizzeria_api.domain.model.Ingredient;
import com.pizzeria_api.domain.model.IngredientUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {

    @Test
    @DisplayName("Should create valid ingredient and deduct/restore stock successfully")
    void shouldCreateAndManageStock() {
        Ingredient cheese = new Ingredient(
                UUID.randomUUID(),
                "Queso Mozzarella",
                IngredientUnit.GRAMS,
                1000,
                new BigDecimal("0.02")
        );

        assertEquals("Queso Mozzarella", cheese.getName());
        assertEquals(1000, cheese.getStock());
        assertEquals(IngredientUnit.GRAMS, cheese.getUnit());

        cheese.deductStock(200);
        assertEquals(800, cheese.getStock());

        cheese.restoreStock(100);
        assertEquals(900, cheese.getStock());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when deducting more than available")
    void shouldFailWhenDeductingMoreThanAvailable() {
        Ingredient flour = new Ingredient(
                UUID.randomUUID(),
                "Harina de Trigo",
                IngredientUnit.GRAMS,
                250,
                new BigDecimal("0.01")
        );

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> flour.deductStock(300)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock for ingredient 'Harina de Trigo'"));
    }

    @Test
    @DisplayName("Should throw InvalidPizzaException on invalid constructor inputs")
    void shouldFailOnInvalidInputs() {
        assertThrows(InvalidPizzaException.class, () -> new Ingredient(
                null, "", IngredientUnit.GRAMS, 100, BigDecimal.ONE
        ));

        assertThrows(InvalidPizzaException.class, () -> new Ingredient(
                null, "Tomate", IngredientUnit.GRAMS, -5, BigDecimal.ONE
        ));

        assertThrows(InvalidPizzaException.class, () -> new Ingredient(
                null, "Tomate", IngredientUnit.GRAMS, 100, new BigDecimal("-1.0")
        ));
    }
}
