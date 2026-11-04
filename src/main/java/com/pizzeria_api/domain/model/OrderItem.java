package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidPizzaException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {
    private final UUID pizzaId;
    private final String pizzaName;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(UUID pizzaId, String pizzaName, int quantity, BigDecimal unitPrice){
        if (pizzaId == null){
            throw new InvalidPizzaException("Pizza ID cannot be null");
        }
        if (quantity <= 0){
            throw new InvalidPizzaException("Quantity must be at least 1");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPizzaException("Unit price must be greater than zero");
        }

        this.pizzaId = pizzaId;
        this.pizzaName = Objects.requireNonNullElse(pizzaName, "Unknow Pizza");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal calculateSubtotal(){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getPizzaId() {
        return pizzaId;
    }

    public String getPizzaName(){
        return pizzaName;
    }

    public int getQuantity(){
        return quantity;
    }

    public BigDecimal getUnitPrice(){
        return unitPrice;
    }
}
