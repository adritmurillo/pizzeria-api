package com.pizzeria_api.adapters.out.persistence.jpa.entity;

import com.pizzeria_api.domain.model.IngredientUnit;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ingredients")
public class IngredientJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private IngredientUnit unit;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "cost_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerUnit;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public IngredientJpaEntity() {
    }

    public IngredientJpaEntity(UUID id, String name, IngredientUnit unit, int stock, BigDecimal costPerUnit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stock = stock;
        this.costPerUnit = costPerUnit;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public IngredientUnit getUnit() { return unit; }
    public void setUnit(IngredientUnit unit) { this.unit = unit; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public BigDecimal getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(BigDecimal costPerUnit) { this.costPerUnit = costPerUnit; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
