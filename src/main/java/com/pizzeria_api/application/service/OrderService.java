package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.exception.CouponNotFoundException;
import com.pizzeria_api.domain.exception.IngredientNotFoundException;
import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.exception.NoAvailableDriverException;
import com.pizzeria_api.domain.exception.OrderNotFoundException;
import com.pizzeria_api.domain.exception.PizzaNotFoundException;
import com.pizzeria_api.domain.model.*;
import com.pizzeria_api.domain.service.DeliveryFleetDispatcher;
import com.pizzeria_api.domain.service.KitchenQueueEstimator;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.in.DispatchOrderUseCase;
import com.pizzeria_api.ports.in.GetOrderUseCase;
import com.pizzeria_api.ports.in.GetOrdersUseCase;
import com.pizzeria_api.ports.in.UpdateOrderStatusUseCase;
import com.pizzeria_api.ports.out.CouponRepositoryPort;
import com.pizzeria_api.ports.out.DeliveryDriverRepositoryPort;
import com.pizzeria_api.ports.out.IngredientRepositoryPort;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class OrderService implements CreateOrderUseCase,
        GetOrderUseCase, UpdateOrderStatusUseCase, GetOrdersUseCase, DispatchOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final PizzaRepositoryPort pizzaRepositoryPort;
    private final IngredientRepositoryPort ingredientRepositoryPort;
    private final CouponRepositoryPort couponRepositoryPort;
    private final DeliveryDriverRepositoryPort deliveryDriverRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;
    private final KitchenQueueEstimator kitchenQueueEstimator = new KitchenQueueEstimator();
    private final DeliveryFleetDispatcher deliveryFleetDispatcher = new DeliveryFleetDispatcher();
    private final Map<String, Order> idempotencyRecords = new ConcurrentHashMap<>();

    @Autowired
    public OrderService(OrderRepositoryPort orderRepositoryPort,
                        PizzaRepositoryPort pizzaRepositoryPort,
                        IngredientRepositoryPort ingredientRepositoryPort,
                        CouponRepositoryPort couponRepositoryPort,
                        DeliveryDriverRepositoryPort deliveryDriverRepositoryPort,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.pizzaRepositoryPort = pizzaRepositoryPort;
        this.ingredientRepositoryPort = ingredientRepositoryPort;
        this.couponRepositoryPort = couponRepositoryPort;
        this.deliveryDriverRepositoryPort = deliveryDriverRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    public OrderService(OrderRepositoryPort orderRepositoryPort,
                        PizzaRepositoryPort pizzaRepositoryPort,
                        IngredientRepositoryPort ingredientRepositoryPort,
                        CouponRepositoryPort couponRepositoryPort,
                        ApplicationEventPublisher eventPublisher) {
        this(orderRepositoryPort, pizzaRepositoryPort, ingredientRepositoryPort, couponRepositoryPort, null, eventPublisher);
    }

    public OrderService(OrderRepositoryPort orderRepositoryPort,
                        PizzaRepositoryPort pizzaRepositoryPort,
                        IngredientRepositoryPort ingredientRepositoryPort,
                        ApplicationEventPublisher eventPublisher) {
        this(orderRepositoryPort, pizzaRepositoryPort, ingredientRepositoryPort, null, null, eventPublisher);
    }

    @Override
    public Order createOrder(CreateOrderCommand command, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Order existingOrder = idempotencyRecords.get(idempotencyKey);
            if (existingOrder != null) {
                return existingOrder;
            }
        }

        List<OrderItem> domainItems = new ArrayList<>();
        for (OrderItemCommand itemCommand : command.items()) {
            Pizza pizza = pizzaRepositoryPort.findById(itemCommand.pizzaId())
                    .orElseThrow(() -> new PizzaNotFoundException("Pizza not found with id: " + itemCommand.pizzaId()));

            if (!pizza.isAvailable()) {
                throw new InvalidOrderException("Pizza '" + pizza.getName() + "' is currently unavailable");
            }

            if (pizza.hasRecipe()) {
                for (RecipeItem recipeItem : pizza.getRecipe()) {
                    int totalRequired = recipeItem.quantityRequired() * itemCommand.quantity();
                    Ingredient ingredient = ingredientRepositoryPort.findById(recipeItem.ingredientId())
                            .orElseThrow(() -> new IngredientNotFoundException(
                                    "Ingredient '" + recipeItem.ingredientName() + "' not found with id: " + recipeItem.ingredientId()
                            ));
                    ingredient.deductStock(totalRequired);
                    ingredientRepositoryPort.save(ingredient);
                }
            } else {
                pizza.deductStock(itemCommand.quantity());
                pizzaRepositoryPort.save(pizza);
            }

            domainItems.add(new OrderItem(
                    pizza.getId(),
                    pizza.getName(),
                    itemCommand.quantity(),
                    pizza.getPrice()
            ));
        }

        Order orderToSave = new Order(
                null,
                command.customerName(),
                command.deliveryAddress(),
                domainItems
        );

        if (command.couponCode() != null && !command.couponCode().isBlank()) {
            if (couponRepositoryPort == null) {
                throw new InvalidOrderException("Coupon repository is not configured");
            }
            String normalizedCode = command.couponCode().trim().toUpperCase(Locale.ROOT);
            Coupon coupon = couponRepositoryPort.findByCode(normalizedCode)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon not found with code: " + normalizedCode));

            BigDecimal discount = coupon.applyTo(orderToSave, LocalDateTime.now());
            orderToSave.applyDiscount(coupon.getCode(), discount);
            couponRepositoryPort.save(coupon);
        }

        int pizzasAhead = orderRepositoryPort.countActivePizzasInQueue();
        int estimatedMinutes = kitchenQueueEstimator.calculateEstimatedWaitMinutes(
                pizzasAhead,
                orderToSave.getTotalPizzaCount()
        );
        orderToSave.assignEstimatedWaitTime(estimatedMinutes);

        Order savedOrder = orderRepositoryPort.save(orderToSave);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyRecords.put(idempotencyKey, savedOrder);
        }

        return savedOrder;
    }

    @Override
    public Order getOrderById(UUID id) {
        return orderRepositoryPort.findById(id).orElseThrow(() -> new
                OrderNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Order updateStatus(UUID id, OrderStatus newStatus) {
        Order order = getOrderById(id);
        switch (newStatus) {
            case PREPARING -> order.markAsPreparing();
            case READY -> {
                order.markAsReady();
                if (deliveryDriverRepositoryPort != null && order.getAssignedDriverId() == null) {
                    List<DeliveryDriver> availableDrivers = deliveryDriverRepositoryPort.findAvailableDrivers();
                    deliveryFleetDispatcher.selectBestDriver(availableDrivers, order.getTotalPizzaCount())
                            .ifPresent(driver -> {
                                driver.assignOrder(order.getId(), order.getTotalPizzaCount());
                                order.assignDriver(driver.getId(), driver.getName());
                                deliveryDriverRepositoryPort.save(driver);
                            });
                }
            }
            case DELIVERED -> {
                order.markAsDelivered();
                if (order.getAssignedDriverId() != null && deliveryDriverRepositoryPort != null) {
                    deliveryDriverRepositoryPort.findById(order.getAssignedDriverId()).ifPresent(driver -> {
                        driver.completeDelivery(order.getId());
                        deliveryDriverRepositoryPort.save(driver);
                    });
                }
            }
            case CANCELLED -> {
                order.cancel();
                for (OrderItem item : order.getItems()) {
                    pizzaRepositoryPort.findById(item.getPizzaId()).ifPresent(p -> {
                        if (p.hasRecipe()) {
                            for (RecipeItem recipeItem : p.getRecipe()) {
                                int totalToRestore = recipeItem.quantityRequired() * item.getQuantity();
                                ingredientRepositoryPort.findById(recipeItem.ingredientId()).ifPresent(ing -> {
                                    ing.restoreStock(totalToRestore);
                                    ingredientRepositoryPort.save(ing);
                                });
                            }
                        } else {
                            p.restoreStock(item.getQuantity());
                            pizzaRepositoryPort.save(p);
                        }
                    });
                }
                if (order.getAppliedCouponCode() != null && couponRepositoryPort != null) {
                    couponRepositoryPort.findByCode(order.getAppliedCouponCode()).ifPresent(coupon -> {
                        coupon.revertRedemption();
                        couponRepositoryPort.save(coupon);
                    });
                }
                if (order.getAssignedDriverId() != null && deliveryDriverRepositoryPort != null) {
                    deliveryDriverRepositoryPort.findById(order.getAssignedDriverId()).ifPresent(driver -> {
                        driver.releaseAssignment();
                        deliveryDriverRepositoryPort.save(driver);
                    });
                }
            }
            case PENDING -> throw new InvalidOrderException("Cannot revert order back to PENDING");
        }

        Order savedOrder = orderRepositoryPort.save(order);

        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getEstimatedWaitMinutes(),
                savedOrder.getEstimatedReadyAt(),
                savedOrder.getAssignedDriverId(),
                savedOrder.getAssignedDriverName()
        ));

        return savedOrder;
    }

    @Override
    public Order dispatchOrder(UUID orderId) {
        if (deliveryDriverRepositoryPort == null) {
            throw new InvalidOrderException("Delivery driver repository is not configured");
        }
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.READY) {
            throw new InvalidOrderException("Only READY orders can be dispatched to a delivery driver");
        }
        if (order.getAssignedDriverId() != null) {
            throw new InvalidOrderException("Order is already assigned to driver '" + order.getAssignedDriverName() + "'");
        }

        List<DeliveryDriver> availableDrivers = deliveryDriverRepositoryPort.findAvailableDrivers();
        DeliveryDriver selectedDriver = deliveryFleetDispatcher
                .selectBestDriver(availableDrivers, order.getTotalPizzaCount())
                .orElseThrow(() -> new NoAvailableDriverException(
                        "No available delivery driver with sufficient thermal capacity for "
                                + order.getTotalPizzaCount() + " pizzas"
                ));

        selectedDriver.assignOrder(order.getId(), order.getTotalPizzaCount());
        order.assignDriver(selectedDriver.getId(), selectedDriver.getName());

        deliveryDriverRepositoryPort.save(selectedDriver);
        Order savedOrder = orderRepositoryPort.save(order);

        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getEstimatedWaitMinutes(),
                savedOrder.getEstimatedReadyAt(),
                savedOrder.getAssignedDriverId(),
                savedOrder.getAssignedDriverName()
        ));

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<Order> getOrders(OrderStatus status, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0) ? 10 : Math.min(size, 50);

        return orderRepositoryPort.findOrders(status, safePage, safeSize);
    }
}
