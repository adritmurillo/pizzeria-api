package com.pizzeria_api.domain.specification;

import com.pizzeria_api.domain.model.Order;

import java.math.BigDecimal;
import java.util.Objects;

public final class MinimumOrderAmountSpecification implements OrderSpecification {

    private final BigDecimal minimumAmount;

    public MinimumOrderAmountSpecification(BigDecimal minimumAmount) {
        this.minimumAmount = Objects.requireNonNull(minimumAmount, "Minimum amount cannot be null");
        if (minimumAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum amount cannot be negative");
        }
    }

    @Override
    public boolean isSatisfiedBy(Order candidate) {
        if (candidate == null || candidate.getSubtotalAmount() == null) {
            return false;
        }
        return candidate.getSubtotalAmount().compareTo(minimumAmount) >= 0;
    }

    public BigDecimal getMinimumAmount() {
        return minimumAmount;
    }
}
