package com.pizzeria_api.adapters.out.persistence.jpa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "pizza_id", nullable = false)
    private UUID pizzaId;

    @Column(name = "pizza_name", nullable = false)
    private String pizzaName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10,
            scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "subtotal", nullable = false, precision = 10, scale
            = 2)
    private BigDecimal subtotal;

    public OrderItemJpaEntity() {
    }

    public OrderItemJpaEntity(UUID id, OrderJpaEntity order, UUID
                                      pizzaId, String pizzaName,
                              int quantity, BigDecimal unitPrice,
                              BigDecimal subtotal) {
        this.id = id;
        this.order = order;
        this.pizzaId = pizzaId;
        this.pizzaName = pizzaName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OrderJpaEntity getOrder() { return order; }
    public void setOrder(OrderJpaEntity order) { this.order = order; }

    public UUID getPizzaId() { return pizzaId; }
    public void setPizzaId(UUID pizzaId) { this.pizzaId = pizzaId; }

    public String getPizzaName() { return pizzaName; }
    public void setPizzaName(String pizzaName) { this.pizzaName =
            pizzaName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice =
            unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal =
            subtotal; }
}