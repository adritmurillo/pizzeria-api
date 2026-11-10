package com.pizzeria_api.domain.service;

import com.pizzeria_api.domain.exception.InvalidOrderException;

public class KitchenQueueEstimator {

    public static final int OVEN_MAX_CAPACITY = 4;
    public static final int PREP_MINUTES_PER_PIZZA = 2;
    public static final int OVEN_BAKE_MINUTES = 10;
    public static final int PACKAGING_DISPATCH_MINUTES = 3;

    public int calculateEstimatedWaitMinutes(int pizzasAheadInQueue, int currentOrderPizzas) {
        if (pizzasAheadInQueue < 0) {
            throw new InvalidOrderException("Pizzas ahead in queue cannot be negative");
        }
        if (currentOrderPizzas <= 0) {
            throw new InvalidOrderException("Current order must contain at least one pizza");
        }

        int totalPizzasToProcess = pizzasAheadInQueue + currentOrderPizzas;
        int ovenBatches = (int) Math.ceil((double) totalPizzasToProcess / OVEN_MAX_CAPACITY);
        int prepMinutes = currentOrderPizzas * PREP_MINUTES_PER_PIZZA;

        return prepMinutes + (ovenBatches * OVEN_BAKE_MINUTES) + PACKAGING_DISPATCH_MINUTES;
    }
}
