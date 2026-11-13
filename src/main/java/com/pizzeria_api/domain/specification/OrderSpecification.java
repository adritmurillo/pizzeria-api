package com.pizzeria_api.domain.specification;

import com.pizzeria_api.domain.model.Order;

import java.util.Objects;

@FunctionalInterface
public interface OrderSpecification {

    boolean isSatisfiedBy(Order candidate);

    default OrderSpecification and(OrderSpecification other) {
        Objects.requireNonNull(other, "Other specification cannot be null");
        return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    default OrderSpecification or(OrderSpecification other) {
        Objects.requireNonNull(other, "Other specification cannot be null");
        return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    default OrderSpecification not() {
        return candidate -> !this.isSatisfiedBy(candidate);
    }
}
