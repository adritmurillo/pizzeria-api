package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InvalidPizzaException;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaTest {
    @Test
    @DisplayName("Should create a pizza successfully")
    void shouldCreateValidPizza(){
        Pizza pizza = new Pizza(
                null,
                "Margherita",
                "Tomato sauce, fresh mozzarella, basil",
                new BigDecimal("12.50"),
                PizzaSize.MEDIUM,
                PizzaCategory.CLASSIC
        );

        assertNotNull(pizza.getId());
        assertEquals("Margherita", pizza.getName());
        assertTrue(pizza.isAvailable());
    }

    @Test
    @DisplayName("Should throw InvalidPizzaException when price is zero or negative")
    void shouldFailWhenPriceIsZeroOrNegative() {
        assertThrows(InvalidPizzaException.class, () -> new Pizza(
                null,
                "Pepperoni",
                "Spicy pepperoni",
                new BigDecimal("0.00"),
                PizzaSize.LARGE,
                PizzaCategory.CLASSIC
        ));
    }

    @Test
    @DisplayName("Should throw InvalidPizzaException when name is too short")
    void shouldFailWhenNameIsTooShort() {
        assertThrows(InvalidPizzaException.class, () -> new Pizza(
                null,
                "Pi",
                "Description",
                new BigDecimal("10.00"),
                PizzaSize.SMALL,
                PizzaCategory.CLASSIC
        ));
    }
}

