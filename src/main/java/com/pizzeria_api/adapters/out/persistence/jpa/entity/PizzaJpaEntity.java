package com.pizzeria_api.adapters.out.persistence.jpa.entity;

import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pizzas")
public class PizzaJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private PizzaSize size;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PizzaCategory category;

    @Column(name = "available", nullable = false)
    private boolean available;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(mappedBy = "pizza", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RecipeItemJpaEntity> recipe = new ArrayList<>();

    public PizzaJpaEntity() {
    }

    public PizzaJpaEntity(UUID id, String name, String description,
                          BigDecimal price, PizzaSize size, PizzaCategory category,
                          boolean available, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.size = size;
        this.category = category;
        this.available = available;
        this.stock = stock;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public PizzaSize getSize() { return size; }
    public void setSize(PizzaSize size) { this.size = size; }

    public PizzaCategory getCategory() { return category; }
    public void setCategory(PizzaCategory category) { this.category = category; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public List<RecipeItemJpaEntity> getRecipe() { return recipe; }
    public void setRecipe(List<RecipeItemJpaEntity> recipe) { this.recipe = recipe; }
}
