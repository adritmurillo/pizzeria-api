package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidOrderException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final String customerName;
    private final String deliveryAddress;
    private final List<OrderItem> items;
    private final BigDecimal subtotalAmount;
    private String appliedCouponCode;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private int estimatedWaitMinutes;
    private LocalDateTime estimatedReadyAt;
    private UUID assignedDriverId;
    private String assignedDriverName;

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items) {
        this(id, customerName, deliveryAddress, items, OrderStatus.PENDING, LocalDateTime.now(), 0, null, null, BigDecimal.ZERO, null, null);
    }

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt) {
        this(id, customerName, deliveryAddress, items, status, createdAt, 0, null, null, BigDecimal.ZERO, null, null);
    }

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt,
                 int estimatedWaitMinutes, LocalDateTime estimatedReadyAt) {
        this(id, customerName, deliveryAddress, items, status, createdAt, estimatedWaitMinutes, estimatedReadyAt, null, BigDecimal.ZERO, null, null);
    }

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt,
                 int estimatedWaitMinutes, LocalDateTime estimatedReadyAt,
                 String appliedCouponCode, BigDecimal discountAmount) {
        this(id, customerName, deliveryAddress, items, status, createdAt, estimatedWaitMinutes, estimatedReadyAt, appliedCouponCode, discountAmount, null, null);
    }

    public Order(UUID id, String customerName, String deliveryAddress, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt,
                 int estimatedWaitMinutes, LocalDateTime estimatedReadyAt,
                 String appliedCouponCode, BigDecimal discountAmount,
                 UUID assignedDriverId, String assignedDriverName) {
        if (customerName == null || customerName.isBlank()) {
            throw new InvalidOrderException("Customer name cannot be empty");
        }
        if (deliveryAddress == null || deliveryAddress.trim().isBlank()) {
            throw new InvalidOrderException("Delivery address cannot be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }
        if (estimatedWaitMinutes < 0) {
            throw new InvalidOrderException("Estimated wait minutes cannot be negative");
        }

        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.customerName = customerName.trim();
        this.deliveryAddress = deliveryAddress.trim();
        this.items = List.copyOf(items);
        this.subtotalAmount = calculateTotal(this.items).setScale(2, RoundingMode.HALF_UP);
        this.appliedCouponCode = (appliedCouponCode == null || appliedCouponCode.isBlank())
                ? null
                : appliedCouponCode.trim().toUpperCase(Locale.ROOT);
        BigDecimal normalizedDiscount = Objects.requireNonNullElse(discountAmount, BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        if (normalizedDiscount.compareTo(BigDecimal.ZERO) < 0 || normalizedDiscount.compareTo(this.subtotalAmount) > 0) {
            throw new InvalidOrderException("Discount amount must be between 0.00 and subtotal amount");
        }
        this.discountAmount = normalizedDiscount;
        this.totalAmount = this.subtotalAmount.subtract(this.discountAmount).setScale(2, RoundingMode.HALF_UP);
        this.status = Objects.requireNonNullElse(status, OrderStatus.PENDING);
        this.createdAt = Objects.requireNonNullElseGet(createdAt, LocalDateTime::now);
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.estimatedReadyAt = Objects.requireNonNullElseGet(
                estimatedReadyAt,
                () -> this.createdAt.plusMinutes(this.estimatedWaitMinutes)
        );
        this.assignedDriverId = assignedDriverId;
        this.assignedDriverName = (assignedDriverName == null || assignedDriverName.isBlank())
                ? null
                : assignedDriverName.trim();
    }

    public void assignDriver(UUID driverId, String driverName) {
        if (driverId == null) {
            throw new InvalidOrderException("Driver ID cannot be null");
        }
        if (driverName == null || driverName.isBlank()) {
            throw new InvalidOrderException("Driver name cannot be empty");
        }
        if (this.status != OrderStatus.READY) {
            throw new InvalidOrderException("Only READY orders can be dispatched to a delivery driver");
        }
        if (this.assignedDriverId != null) {
            throw new InvalidOrderException("Order is already assigned to driver '" + this.assignedDriverName + "'");
        }
        this.assignedDriverId = driverId;
        this.assignedDriverName = driverName.trim();
    }

    public void applyDiscount(String couponCode, BigDecimal discountAmount) {
        if (couponCode == null || couponCode.isBlank()) {
            throw new InvalidOrderException("Coupon code cannot be empty when applying a discount");
        }
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderException("Discount amount cannot be negative");
        }
        BigDecimal scaledDiscount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        if (scaledDiscount.compareTo(this.subtotalAmount) > 0) {
            throw new InvalidOrderException("Discount amount cannot exceed order subtotal");
        }
        this.appliedCouponCode = couponCode.trim().toUpperCase(Locale.ROOT);
        this.discountAmount = scaledDiscount;
        this.totalAmount = this.subtotalAmount.subtract(this.discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalPizzaCount() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public void assignEstimatedWaitTime(int minutes) {
        if (minutes < 0) {
            throw new InvalidOrderException("Estimated wait minutes cannot be negative");
        }
        this.estimatedWaitMinutes = minutes;
        this.estimatedReadyAt = this.createdAt.plusMinutes(minutes);
    }

    public void markAsPreparing() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderException("Only PENDING orders can be moved to PREPARING");
        }
        this.status = OrderStatus.PREPARING;
    }

    public void markAsReady() {
        if (this.status != OrderStatus.PREPARING) {
            throw new InvalidOrderException("Only PREPARING orders can be moved to READY");
        }
        this.status = OrderStatus.READY;
        this.estimatedWaitMinutes = 0;
    }

    public void markAsDelivered() {
        if (this.status != OrderStatus.READY) {
            throw new InvalidOrderException("Only READY orders can be moved to DELIVERED");
        }
        this.status = OrderStatus.DELIVERED;
        this.estimatedWaitMinutes = 0;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Order is already cancelled");
        }
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Cannot cancel an order that has already been delivered");
        }
        this.status = OrderStatus.CANCELLED;
        this.estimatedWaitMinutes = 0;
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

    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
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

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public LocalDateTime getEstimatedReadyAt() {
        return estimatedReadyAt;
    }

    public UUID getAssignedDriverId() {
        return assignedDriverId;
    }

    public String getAssignedDriverName() {
        return assignedDriverName;
    }
}
