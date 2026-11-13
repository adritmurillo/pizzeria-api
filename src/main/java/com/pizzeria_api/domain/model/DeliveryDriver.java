package com.pizzeria_api.domain.model;

import com.pizzeria_api.domain.exception.InvalidDriverException;

import java.util.Objects;
import java.util.UUID;

public class DeliveryDriver {

    private final UUID id;
    private final String name;
    private final String phone;
    private final VehicleType vehicleType;
    private final int maxCapacityPizzas;
    private DriverStatus status;
    private UUID currentOrderId;
    private int completedDeliveries;

    public DeliveryDriver(UUID id,
                          String name,
                          String phone,
                          VehicleType vehicleType,
                          int maxCapacityPizzas,
                          DriverStatus status,
                          UUID currentOrderId,
                          int completedDeliveries) {
        if (name == null || name.isBlank()) {
            throw new InvalidDriverException("Driver name cannot be empty");
        }
        if (phone == null || phone.isBlank()) {
            throw new InvalidDriverException("Driver phone cannot be empty");
        }
        if (vehicleType == null) {
            throw new InvalidDriverException("Vehicle type is required");
        }
        if (maxCapacityPizzas <= 0) {
            throw new InvalidDriverException("Maximum pizza capacity must be greater than zero");
        }
        if (completedDeliveries < 0) {
            throw new InvalidDriverException("Completed deliveries cannot be negative");
        }

        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.name = name.trim();
        this.phone = phone.trim();
        this.vehicleType = vehicleType;
        this.maxCapacityPizzas = maxCapacityPizzas;
        this.status = Objects.requireNonNullElse(status, DriverStatus.AVAILABLE);
        this.currentOrderId = currentOrderId;
        this.completedDeliveries = completedDeliveries;

        if (this.status == DriverStatus.ON_DELIVERY && this.currentOrderId == null) {
            throw new InvalidDriverException("Driver with ON_DELIVERY status must have a currentOrderId");
        }
    }

    public boolean canCarry(int pizzaCount) {
        return this.status == DriverStatus.AVAILABLE
                && pizzaCount > 0
                && pizzaCount <= this.maxCapacityPizzas;
    }

    public void assignOrder(UUID orderId, int orderPizzaCount) {
        if (orderId == null) {
            throw new InvalidDriverException("Order ID cannot be null when assigning a driver");
        }
        if (this.status != DriverStatus.AVAILABLE) {
            throw new InvalidDriverException("Driver '" + this.name + "' is not available for dispatch");
        }
        if (orderPizzaCount <= 0) {
            throw new InvalidDriverException("Order pizza count must be greater than zero");
        }
        if (orderPizzaCount > this.maxCapacityPizzas) {
            throw new InvalidDriverException(
                    "Order with " + orderPizzaCount + " pizzas exceeds driver '" + this.name
                            + "' thermal bag capacity of " + this.maxCapacityPizzas
            );
        }

        this.status = DriverStatus.ON_DELIVERY;
        this.currentOrderId = orderId;
    }

    public void completeDelivery(UUID orderId) {
        if (this.status != DriverStatus.ON_DELIVERY) {
            throw new InvalidDriverException("Driver '" + this.name + "' is not currently on a delivery");
        }
        if (orderId != null && !Objects.equals(this.currentOrderId, orderId)) {
            throw new InvalidDriverException("Driver '" + this.name + "' is assigned to a different order");
        }

        this.status = DriverStatus.AVAILABLE;
        this.currentOrderId = null;
        this.completedDeliveries++;
    }

    public void releaseAssignment() {
        if (this.status == DriverStatus.ON_DELIVERY) {
            this.status = DriverStatus.AVAILABLE;
            this.currentOrderId = null;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getMaxCapacityPizzas() {
        return maxCapacityPizzas;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public UUID getCurrentOrderId() {
        return currentOrderId;
    }

    public int getCompletedDeliveries() {
        return completedDeliveries;
    }
}
