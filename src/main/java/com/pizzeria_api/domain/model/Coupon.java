package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidCouponException;
import com.pizzeria_api.domain.specification.MinimumOrderAmountSpecification;
import com.pizzeria_api.domain.specification.MinimumPizzaCountSpecification;
import com.pizzeria_api.domain.specification.OrderSpecification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final UUID id;
    private final String code;
    private final DiscountType discountType;
    private final BigDecimal discountValue;
    private final BigDecimal minOrderAmount;
    private final int minPizzaCount;
    private final int maxUses;
    private int currentUses;
    private final LocalDateTime validUntil;
    private boolean active;

    public Coupon(UUID id,
                  String code,
                  DiscountType discountType,
                  BigDecimal discountValue,
                  BigDecimal minOrderAmount,
                  int minPizzaCount,
                  int maxUses,
                  int currentUses,
                  LocalDateTime validUntil,
                  boolean active) {
        if (code == null || code.isBlank()) {
            throw new InvalidCouponException("Coupon code cannot be empty");
        }
        if (discountType == null) {
            throw new InvalidCouponException("Discount type is required");
        }
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCouponException("Discount value must be greater than zero");
        }
        if (discountType == DiscountType.PERCENTAGE && discountValue.compareTo(ONE_HUNDRED) > 0) {
            throw new InvalidCouponException("Percentage discount cannot exceed 100%");
        }
        if (minOrderAmount != null && minOrderAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidCouponException("Minimum order amount cannot be negative");
        }
        if (minPizzaCount < 0) {
            throw new InvalidCouponException("Minimum pizza count cannot be negative");
        }
        if (maxUses <= 0) {
            throw new InvalidCouponException("Maximum uses must be greater than zero");
        }
        if (currentUses < 0 || currentUses > maxUses) {
            throw new InvalidCouponException("Current uses must be between 0 and maxUses");
        }
        if (validUntil == null) {
            throw new InvalidCouponException("Coupon expiration date (validUntil) is required");
        }

        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.code = code.trim().toUpperCase(Locale.ROOT);
        this.discountType = discountType;
        this.discountValue = discountValue.setScale(2, RoundingMode.HALF_UP);
        this.minOrderAmount = Objects.requireNonNullElse(minOrderAmount, BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        this.minPizzaCount = minPizzaCount;
        this.maxUses = maxUses;
        this.currentUses = currentUses;
        this.validUntil = validUntil;
        this.active = active;
    }

    public OrderSpecification buildEligibilitySpecification() {
        OrderSpecification minAmountSpec = new MinimumOrderAmountSpecification(this.minOrderAmount);
        OrderSpecification minPizzasSpec = new MinimumPizzaCountSpecification(this.minPizzaCount);
        return minAmountSpec.and(minPizzasSpec);
    }

    public BigDecimal applyTo(Order order, LocalDateTime evaluationTime) {
        Objects.requireNonNull(order, "Order cannot be null");
        LocalDateTime now = Objects.requireNonNullElseGet(evaluationTime, LocalDateTime::now);

        if (!this.active) {
            throw new InvalidCouponException("Coupon '" + this.code + "' is inactive");
        }
        if (now.isAfter(this.validUntil)) {
            throw new InvalidCouponException("Coupon '" + this.code + "' has expired");
        }
        if (this.currentUses >= this.maxUses) {
            throw new InvalidCouponException("Coupon '" + this.code + "' has reached its usage limit");
        }

        OrderSpecification minAmountSpec = new MinimumOrderAmountSpecification(this.minOrderAmount);
        OrderSpecification minPizzasSpec = new MinimumPizzaCountSpecification(this.minPizzaCount);
        OrderSpecification combinedSpec = minAmountSpec.and(minPizzasSpec);

        if (!combinedSpec.isSatisfiedBy(order)) {
            if (!minAmountSpec.isSatisfiedBy(order)) {
                throw new InvalidCouponException(
                        "Coupon '" + this.code + "' requires a minimum order subtotal of " + this.minOrderAmount
                );
            }
            throw new InvalidCouponException(
                    "Coupon '" + this.code + "' requires at least " + this.minPizzaCount + " pizzas in the order"
            );
        }

        BigDecimal subtotal = order.getSubtotalAmount();
        BigDecimal rawDiscount = switch (this.discountType) {
            case PERCENTAGE -> subtotal.multiply(this.discountValue)
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> this.discountValue;
        };

        BigDecimal effectiveDiscount = rawDiscount.min(subtotal).setScale(2, RoundingMode.HALF_UP);
        this.currentUses++;
        if (this.currentUses >= this.maxUses) {
            this.active = false;
        }

        return effectiveDiscount;
    }

    public void revertRedemption() {
        if (this.currentUses > 0) {
            this.currentUses--;
            if (this.currentUses < this.maxUses) {
                this.active = true;
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public int getMinPizzaCount() {
        return minPizzaCount;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getCurrentUses() {
        return currentUses;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public boolean isActive() {
        return active;
    }
}
