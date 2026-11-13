package com.pizzeria_api.domain.service;

import com.pizzeria_api.domain.model.DeliveryDriver;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DeliveryFleetDispatcher {

    private static final Comparator<DeliveryDriver> OPTIMAL_DRIVER_COMPARATOR =
            Comparator.comparingInt(DeliveryDriver::getCompletedDeliveries)
                    .thenComparingInt(DeliveryDriver::getMaxCapacityPizzas)
                    .thenComparing(DeliveryDriver::getId);

    /**
     * Selects the optimal available driver in O(D) time and O(1) space using a single-pass minimum reduction.
     * Criteria:
     * 1. Must be AVAILABLE and have sufficient thermal capacity (canCarry(orderPizzaCount)).
     * 2. Least completed deliveries (fair workload balancing across the fleet).
     * 3. Best-fit thermal bag capacity (reserves larger vehicles like CARs for larger orders).
     * 4. Deterministic tie-breaker by driver UUID.
     */
    public Optional<DeliveryDriver> selectBestDriver(List<DeliveryDriver> candidateDrivers, int orderPizzaCount) {
        if (candidateDrivers == null || candidateDrivers.isEmpty() || orderPizzaCount <= 0) {
            return Optional.empty();
        }

        return candidateDrivers.stream()
                .filter(driver -> driver != null && driver.canCarry(orderPizzaCount))
                .min(OPTIMAL_DRIVER_COMPARATOR);
    }
}
