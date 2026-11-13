package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InvalidDriverException;
import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.domain.model.DriverStatus;
import com.pizzeria_api.domain.model.VehicleType;
import com.pizzeria_api.domain.service.DeliveryFleetDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryFleetDispatcherTest {

    private final DeliveryFleetDispatcher dispatcher = new DeliveryFleetDispatcher();

    @Test
    @DisplayName("Should select driver with least completed deliveries and best-fit thermal capacity in O(D)")
    void shouldSelectLeastLoadedAndBestFitDriver() {
        DeliveryDriver eBikeDriver = new DeliveryDriver(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "Mateo Rossi", "+51-999-100-201", VehicleType.E_BIKE, 4, DriverStatus.AVAILABLE, null, 2
        );
        DeliveryDriver motoDriverLessLoaded = new DeliveryDriver(
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "Valeria Silva", "+51-999-100-202", VehicleType.MOTORCYCLE, 8, DriverStatus.AVAILABLE, null, 1
        );
        DeliveryDriver carDriverSameLoad = new DeliveryDriver(
                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                "Diego Mendoza", "+51-999-100-203", VehicleType.CAR, 20, DriverStatus.AVAILABLE, null, 1
        );

        Optional<DeliveryDriver> selected = dispatcher.selectBestDriver(
                List.of(eBikeDriver, carDriverSameLoad, motoDriverLessLoaded),
                3
        );

        assertTrue(selected.isPresent());
        assertEquals("Valeria Silva", selected.get().getName());
    }

    @Test
    @DisplayName("Should filter out drivers whose thermal bag capacity is smaller than order pizza count")
    void shouldFilterByThermalCapacity() {
        DeliveryDriver eBikeDriver = new DeliveryDriver(
                UUID.randomUUID(), "Mateo Rossi", "+51-999-100-201", VehicleType.E_BIKE, 4, DriverStatus.AVAILABLE, null, 0
        );
        DeliveryDriver motoDriver = new DeliveryDriver(
                UUID.randomUUID(), "Valeria Silva", "+51-999-100-202", VehicleType.MOTORCYCLE, 8, DriverStatus.AVAILABLE, null, 5
        );

        Optional<DeliveryDriver> selectedForSixPizzas = dispatcher.selectBestDriver(
                List.of(eBikeDriver, motoDriver),
                6
        );

        assertTrue(selectedForSixPizzas.isPresent());
        assertEquals("Valeria Silva", selectedForSixPizzas.get().getName());

        Optional<DeliveryDriver> selectedForTenPizzas = dispatcher.selectBestDriver(
                List.of(eBikeDriver, motoDriver),
                10
        );
        assertTrue(selectedForTenPizzas.isEmpty());
    }

    @Test
    @DisplayName("Should enforce driver state invariants on assignOrder and completeDelivery")
    void shouldEnforceDriverLifecycleInvariants() {
        DeliveryDriver driver = new DeliveryDriver(
                UUID.randomUUID(), "Mateo Rossi", "+51-999-100-201", VehicleType.E_BIKE, 4, DriverStatus.AVAILABLE, null, 0
        );
        UUID orderId = UUID.randomUUID();

        assertThrows(InvalidDriverException.class, () -> driver.assignOrder(orderId, 5));

        driver.assignOrder(orderId, 3);
        assertEquals(DriverStatus.ON_DELIVERY, driver.getStatus());
        assertEquals(orderId, driver.getCurrentOrderId());

        assertThrows(InvalidDriverException.class, () -> driver.assignOrder(UUID.randomUUID(), 1));

        driver.completeDelivery(orderId);
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
        assertNull(driver.getCurrentOrderId());
        assertEquals(1, driver.getCompletedDeliveries());
    }
}
