package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InsufficientStockException;
import com.pizzeria_api.domain.exception.InvalidPizzaException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Ingredient {
    private final UUID id;
    private String name;
    private IngredientUnit unit;
    private int stock;
    private BigDecimal costPerUnit;

    public Ingredient(UUID id, String name, IngredientUnit unit, int stock, BigDecimal costPerUnit) {
        validateName(name);
        validateStock(stock);
        validateCost(costPerUnit);

        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.name = name.trim();
        this.unit = Objects.requireNonNullElse(unit, IngredientUnit.UNITS);
        this.stock = stock;
        this.costPerUnit = costPerUnit;
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new InvalidPizzaException("Ingredient name must have at least 2 characters");
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new InvalidPizzaException("Ingredient stock cannot be negative");
        }
    }

    private void validateCost(BigDecimal cost) {
        if (cost == null || cost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPizzaException("Cost per unit cannot be negative");
        }
    }

    public void deductStock(int amount) {
        if (amount <= 0) {
            throw new InvalidPizzaException("Amount to deduct must be greater than zero");
        }
        if (this.stock < amount) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for ingredient '%s'. Requested: %d %s, available: %d %s",
                    this.name, amount, this.unit, this.stock, this.unit
            ));
        }
        this.stock -= amount;
    }

    public void restoreStock(int amount) {
        if (amount <= 0) {
            throw new InvalidPizzaException("Amount to restore must be greater than zero");
        }
        this.stock += amount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public int getStock() {
        return stock;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }
}
