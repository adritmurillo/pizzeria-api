package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidOrderException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final String customerName;
    private final String deliveryAddress;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items) {
        this(id, customerName, deliveryAddress, items, OrderStatus.PENDING, LocalDateTime.now());
    }

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items, OrderStatus status, LocalDateTime createdAt) {
        if (customerName == null || customerName.isBlank()){
            throw new InvalidOrderException("Customer name cannot be empty");
        }
        if (deliveryAddress == null || deliveryAddress.trim().isBlank()){
            throw new InvalidOrderException("Delivery address cannot be empty");
        }
        if (items == null ||items.isEmpty()){
            throw new InvalidOrderException("Order must contain at least one item");
        }

        this.id = Objects.requireNonNullElseGet(id, UUID :: randomUUID);
        this.customerName = customerName.trim();
        this.deliveryAddress = deliveryAddress.trim();
        this.items = List.copyOf(items);
        this.totalAmount = calculateTotal(this.items);
        this.status = Objects.requireNonNullElse(status, OrderStatus.PENDING);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
    }

    private BigDecimal calculateTotal(List<OrderItem> items){
        return items.stream().map(OrderItem :: calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markAsPreparing(){
        if (this.status != OrderStatus.PENDING){
            throw new InvalidOrderException("Only PENDING orders can be moved to PREPARING");
        }
        this.status = OrderStatus.PREPARING;
    }

    public void markAsReady(){
        if (this.status != OrderStatus.PREPARING){
            throw new InvalidOrderException("Only PREPARING orders can be moved to READY");
        }
        this.status = OrderStatus.READY;
    }

    public void markAsDelivered(){
        if (this.status != OrderStatus.READY){
            throw new InvalidOrderException("Only READY orders can be moved to DELIVERED");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order is already cancelled");
        }
        if (this.status == OrderStatus.DELIVERED){
            throw new InvalidOrderException("Cannot cancel an order that has already been delivered");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
