package com.pizzeria_api.adapters.out.persistence.jpa.entity;

import com.pizzeria_api.domain.model.IngredientUnit;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "pizza_recipes")
public class RecipeItemJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_id", nullable = false)
    private PizzaJpaEntity pizza;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private IngredientJpaEntity ingredient;

    @Column(name = "quantity_required", nullable = false)
    private int quantityRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private IngredientUnit unit;

    public RecipeItemJpaEntity() {
    }

    public RecipeItemJpaEntity(UUID id, PizzaJpaEntity pizza, IngredientJpaEntity ingredient, int quantityRequired, IngredientUnit unit) {
        this.id = id;
        this.pizza = pizza;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PizzaJpaEntity getPizza() { return pizza; }
    public void setPizza(PizzaJpaEntity pizza) { this.pizza = pizza; }

    public IngredientJpaEntity getIngredient() { return ingredient; }
    public void setIngredient(IngredientJpaEntity ingredient) { this.ingredient = ingredient; }

    public int getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(int quantityRequired) { this.quantityRequired = quantityRequired; }

    public IngredientUnit getUnit() { return unit; }
    public void setUnit(IngredientUnit unit) { this.unit = unit; }
}
