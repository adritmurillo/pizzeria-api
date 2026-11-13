package com.pizzeria_api.adapters.out.persistence.jpa.entity;

import com.pizzeria_api.domain.model.DriverStatus;
import com.pizzeria_api.domain.model.VehicleType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "delivery_drivers")
public class DeliveryDriverJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(name = "max_capacity_pizzas", nullable = false)
    private int maxCapacityPizzas;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DriverStatus status;

    @Column(name = "current_order_id")
    private UUID currentOrderId;

    @Column(name = "completed_deliveries", nullable = false)
    private int completedDeliveries;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public DeliveryDriverJpaEntity() {
    }

    public DeliveryDriverJpaEntity(UUID id,
                                   String name,
                                   String phone,
                                   VehicleType vehicleType,
                                   int maxCapacityPizzas,
                                   DriverStatus status,
                                   UUID currentOrderId,
                                   int completedDeliveries) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.maxCapacityPizzas = maxCapacityPizzas;
        this.status = status;
        this.currentOrderId = currentOrderId;
        this.completedDeliveries = completedDeliveries;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public int getMaxCapacityPizzas() { return maxCapacityPizzas; }
    public void setMaxCapacityPizzas(int maxCapacityPizzas) { this.maxCapacityPizzas = maxCapacityPizzas; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public UUID getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(UUID currentOrderId) { this.currentOrderId = currentOrderId; }

    public int getCompletedDeliveries() { return completedDeliveries; }
    public void setCompletedDeliveries(int completedDeliveries) { this.completedDeliveries = completedDeliveries; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
