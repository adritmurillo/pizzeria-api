package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InsufficientStockException;
import com.pizzeria_api.domain.exception.InvalidPizzaException;

import java.math.BigDecimal;
import java.util.List;
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
    private int stock;
    private List<RecipeItem> recipe;

    public Pizza(UUID id, String name, String description, BigDecimal price,
                 PizzaSize size, PizzaCategory category) {
        this(id, name, description, price, size, category, true, 50, List.of());
    }

    public Pizza(UUID id, String name, String description, BigDecimal price,
                 PizzaSize size, PizzaCategory category, boolean available) {
        this(id, name, description, price, size, category, available, 50, List.of());
    }

    public Pizza(UUID id, String name, String description, BigDecimal price,
                 PizzaSize size, PizzaCategory category, boolean available, int stock) {
        this(id, name, description, price, size, category, available, stock, List.of());
    }

    public Pizza(UUID id, String name, String description, BigDecimal price,
                 PizzaSize size, PizzaCategory category, boolean available, int stock,
                 List<RecipeItem> recipe) {
        validateName(name);
        validatePrice(price);
        validateSize(size);
        validateCategory(category);
        validateStock(stock);

        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.price = price;
        this.size = size;
        this.category = category;
        this.stock = stock;
        this.available = available && (stock > 0);
        this.recipe = recipe != null ? List.copyOf(recipe) : List.of();
    }

    private void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new InvalidPizzaException("Pizza name must have at least 3 characters");
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPizzaException("Pizza price must be greater than zero");
        }
    }

    private void validateCategory(PizzaCategory category) {
        if (category == null) {
            throw new InvalidPizzaException("Pizza category must be specified");
        }
    }

    private void validateSize(PizzaSize size) {
        if (size == null) {
            throw new InvalidPizzaException("Pizza size must be specified");
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new InvalidPizzaException("Stock cannot be negative");
        }
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidPizzaException("Quantity to deduct must be greater than zero");
        }
        if (this.stock < quantity) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock for pizza '%s'. Requested: %d, available: %d",
                    this.name, quantity, this.stock
            ));
        }

        this.stock -= quantity;
        if (this.stock == 0) {
            this.available = false;
        }
    }

    public void restoreStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidPizzaException("Quantity to restore must be greater than zero");
        }
        this.stock += quantity;
        this.available = true;
    }

    public void markAsUnavailable() {
        this.available = false;
    }

    public void markAsAvailable() {
        if (this.stock > 0) {
            this.available = true;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public PizzaSize getSize() {
        return size;
    }

    public PizzaCategory getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getStock() {
        return stock;
    }

    public List<RecipeItem> getRecipe() {
        return recipe;
    }

    public boolean hasRecipe() {
        return recipe != null && !recipe.isEmpty();
    }

    public void assignRecipe(List<RecipeItem> recipe) {
        this.recipe = recipe != null ? List.copyOf(recipe) : List.of();
    }
}
