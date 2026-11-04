package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidPizzaException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Pizza {
    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private PizzaSize size;
    private PizzaCategory category;
    private boolean available;

    public Pizza(UUID id, String name, String description, BigDecimal price,
                 PizzaSize size, PizzaCategory category){
        validateName(name);
        validatePrice(price);
        validateSize(size);
        validateCategory(category);

        this.id = Objects.requireNonNullElseGet(id, UUID :: randomUUID);
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.price = price;
        this.size = size;
        this.category = category;
        this.available = true;
    }

    private void validateName(String name){
        if (name == null || name.trim().length() < 3){
            throw new InvalidPizzaException("Pizza name must have at least 3 characters");
        }
    }
    private void validatePrice(BigDecimal price){
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidPizzaException("Pizza price must be greater than zero");
        }
    }
    private void validateCategory(PizzaCategory category){
        if (category == null) {
            throw new InvalidPizzaException("Pizza category must be specified");
        }
    }
    private void validateSize(PizzaSize size){
        if (size == null){
            throw new InvalidPizzaException("Pizza size must be specified");
        }
    }

    public void markAsUnavailable(){
        this.available = false;
    }

    public void markAsAvailable(){
        this.available = true;
    }

    public UUID getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public PizzaSize getSize(){
        return size;
    }

    public PizzaCategory getCategory(){
        return category;
    }

    public boolean isAvailable(){
        return available;
    }
}
