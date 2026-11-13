package com.pizzeria_api.domain.specification;

import com.pizzeria_api.domain.model.Order;

public final class MinimumPizzaCountSpecification implements OrderSpecification {

    private final int minimumPizzaCount;

    public MinimumPizzaCountSpecification(int minimumPizzaCount) {
        if (minimumPizzaCount < 0) {
            throw new IllegalArgumentException("Minimum pizza count cannot be negative");
        }
        this.minimumPizzaCount = minimumPizzaCount;
    }

    @Override
    public boolean isSatisfiedBy(Order candidate) {
        if (candidate == null) {
            return false;
        }
        return candidate.getTotalPizzaCount() >= minimumPizzaCount;
    }

    public int getMinimumPizzaCount() {
        return minimumPizzaCount;
    }
}
