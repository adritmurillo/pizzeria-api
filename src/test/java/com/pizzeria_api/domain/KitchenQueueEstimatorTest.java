package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.service.KitchenQueueEstimator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KitchenQueueEstimatorTest {

    private final KitchenQueueEstimator estimator = new KitchenQueueEstimator();

    @Test
    @DisplayName("Should calculate 17 minutes when kitchen is empty and order has 2 pizzas (1 oven batch)")
    void shouldCalculateEtaForEmptyQueue() {
        // 2 pizzas * 2 min prep (4) + 1 batch * 10 min bake (10) + 3 min packaging = 17 min
        int waitMinutes = estimator.calculateEstimatedWaitMinutes(0, 2);
        assertEquals(17, waitMinutes);
    }

    @Test
    @DisplayName("Should calculate 37 minutes when 8 pizzas are ahead and order has 2 pizzas (3 oven batches)")
    void shouldCalculateEtaForBusyQueue() {
        // Total pizzas = 8 + 2 = 10 -> ceil(10 / 4.0) = 3 batches (30 min) + 4 min prep + 3 min packaging = 37 min
        int waitMinutes = estimator.calculateEstimatedWaitMinutes(8, 2);
        assertEquals(37, waitMinutes);
    }

    @Test
    @DisplayName("Should throw InvalidOrderException when pizzas ahead is negative")
    void shouldFailWhenQueueIsNegative() {
        assertThrows(InvalidOrderException.class, () ->
                estimator.calculateEstimatedWaitMinutes(-1, 2));
    }

    @Test
    @DisplayName("Should throw InvalidOrderException when current order pizzas is zero or negative")
    void shouldFailWhenOrderPizzasIsZero() {
        assertThrows(InvalidOrderException.class, () ->
                estimator.calculateEstimatedWaitMinutes(0, 0));
    }
}
