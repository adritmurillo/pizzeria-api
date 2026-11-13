package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.*;
import com.pizzeria_api.ports.in.CreateOrderUseCase.CreateOrderCommand;
import com.pizzeria_api.ports.in.CreateOrderUseCase.OrderItemCommand;
import com.pizzeria_api.ports.out.CouponRepositoryPort;
import com.pizzeria_api.ports.out.DeliveryDriverRepositoryPort;
import com.pizzeria_api.ports.out.IngredientRepositoryPort;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private PizzaRepositoryPort pizzaRepositoryPort;

    @Mock
    private IngredientRepositoryPort ingredientRepositoryPort;

    @Mock
    private CouponRepositoryPort couponRepositoryPort;

    @Mock
    private DeliveryDriverRepositoryPort deliveryDriverRepositoryPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should restore pizza stock when order is cancelled (legacy fallback)")
    void shouldRestoreStockWhenOrderIsCancelled() {
        UUID orderId = UUID.randomUUID();
        UUID pizzaId = UUID.randomUUID();

        Pizza pizza = new Pizza(pizzaId, "Brava", "Extra cheese", new BigDecimal("15.00"),
                PizzaSize.LARGE, PizzaCategory.CLASSIC, true, 48);

        OrderItem item = new OrderItem(pizzaId, "Brava", 2, new BigDecimal("15.00"));
        Order order = new Order(orderId, "Adrian Murillo", "Av. El Muro 442", List.of(item));

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(pizzaRepositoryPort.findById(pizzaId)).thenReturn(Optional.of(pizza));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updatedOrder = orderService.updateStatus(orderId, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, updatedOrder.getStatus());
        assertEquals(50, pizza.getStock());
        verify(pizzaRepositoryPort, times(1)).save(pizza);
        verify(orderRepositoryPort, times(1)).save(order);
        verify(eventPublisher, times(1)).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    @DisplayName("Should create order with dynamic ETA calculated from kitchen queue")
    void shouldCreateOrderWithDynamicEta() {
        UUID pizzaId = UUID.randomUUID();
        Pizza pizza = new Pizza(pizzaId, "Brava", "Description", new BigDecimal("15.00"),
                PizzaSize.LARGE, PizzaCategory.CLASSIC, true, 20);

        when(pizzaRepositoryPort.findById(pizzaId)).thenReturn(Optional.of(pizza));
        when(orderRepositoryPort.countActivePizzasInQueue()).thenReturn(0);
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderCommand command = new CreateOrderCommand(
                "Carlos ZeroTrust",
                "Av. Central 123",
                List.of(new OrderItemCommand(pizzaId, 2))
        );

        Order createdOrder = orderService.createOrder(command, null);

        assertNotNull(createdOrder);
        assertEquals(17, createdOrder.getEstimatedWaitMinutes());
        assertNotNull(createdOrder.getEstimatedReadyAt());
        assertEquals(18, pizza.getStock());
    }

    @Test
    @DisplayName("Should deduct ingredients stock when pizza has BOM recipe")
    void shouldDeductIngredientStockWhenPizzaHasRecipe() {
        UUID pizzaId = UUID.randomUUID();
        UUID cheeseId = UUID.randomUUID();

        Ingredient cheese = new Ingredient(cheeseId, "Queso Mozzarella", IngredientUnit.GRAMS, 1000, new BigDecimal("0.02"));
        RecipeItem recipeItem = new RecipeItem(cheeseId, "Queso Mozzarella", 200, IngredientUnit.GRAMS);

        Pizza pizzaWithRecipe = new Pizza(pizzaId, "Margarita Gourmet", "Con mozzarella", new BigDecimal("18.00"),
                PizzaSize.MEDIUM, PizzaCategory.CLASSIC, true, 50, List.of(recipeItem));

        when(pizzaRepositoryPort.findById(pizzaId)).thenReturn(Optional.of(pizzaWithRecipe));
        when(ingredientRepositoryPort.findById(cheeseId)).thenReturn(Optional.of(cheese));
        when(orderRepositoryPort.countActivePizzasInQueue()).thenReturn(0);
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderCommand command = new CreateOrderCommand(
                "Lucas Chef",
                "Calle Gourmet 44",
                List.of(new OrderItemCommand(pizzaId, 3)) // 3 pizzas * 200g = 600g
        );

        Order createdOrder = orderService.createOrder(command, null);

        assertNotNull(createdOrder);
        assertEquals(400, cheese.getStock()); // 1000 - 600 = 400
        verify(ingredientRepositoryPort, times(1)).save(cheese);
    }

    @Test
    @DisplayName("Should apply promotional coupon discount when OrderSpecification is satisfied")
    void shouldApplyCouponDiscountWhenSpecificationIsSatisfied() {
        UUID pizzaId = UUID.randomUUID();
        Pizza pizza = new Pizza(pizzaId, "Brava", "Especial", new BigDecimal("20.00"),
                PizzaSize.LARGE, PizzaCategory.SPECIALITY, true, 30);

        Coupon welcome10 = new Coupon(
                UUID.randomUUID(),
                "WELCOME10",
                DiscountType.PERCENTAGE,
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                1,
                100,
                0,
                LocalDateTime.now().plusDays(10),
                true
        );

        when(pizzaRepositoryPort.findById(pizzaId)).thenReturn(Optional.of(pizza));
        when(couponRepositoryPort.findByCode("WELCOME10")).thenReturn(Optional.of(welcome10));
        when(orderRepositoryPort.countActivePizzasInQueue()).thenReturn(0);
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderCommand command = new CreateOrderCommand(
                "Joaquin Tumba",
                "Av. Los Incas 500",
                List.of(new OrderItemCommand(pizzaId, 2)), // 40.00 subtotal
                "welcome10"
        );

        Order createdOrder = orderService.createOrder(command, null);

        assertEquals(new BigDecimal("40.00"), createdOrder.getSubtotalAmount());
        assertEquals("WELCOME10", createdOrder.getAppliedCouponCode());
        assertEquals(new BigDecimal("4.00"), createdOrder.getDiscountAmount());
        assertEquals(new BigDecimal("36.00"), createdOrder.getTotalAmount());
        assertEquals(1, welcome10.getCurrentUses());
        verify(couponRepositoryPort, times(1)).save(welcome10);
    }

    @Test
    @DisplayName("Should auto-dispatch best driver when order transitions to READY and release driver on DELIVERED")
    void shouldAutoDispatchDriverOnReadyAndReleaseOnDelivered() {
        UUID orderId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        OrderItem item = new OrderItem(UUID.randomUUID(), "Brava", 2, new BigDecimal("20.00"));
        Order order = new Order(orderId, "Joaquin", "Av. Pardo 300", List.of(item), OrderStatus.PREPARING, LocalDateTime.now());

        DeliveryDriver driver = new DeliveryDriver(
                driverId, "Mateo Rossi", "+51-999-100-201", VehicleType.E_BIKE, 4, DriverStatus.AVAILABLE, null, 0
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(deliveryDriverRepositoryPort.findAvailableDrivers()).thenReturn(List.of(driver));
        when(deliveryDriverRepositoryPort.findById(driverId)).thenReturn(Optional.of(driver));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order readyOrder = orderService.updateStatus(orderId, OrderStatus.READY);

        assertEquals(OrderStatus.READY, readyOrder.getStatus());
        assertEquals(driverId, readyOrder.getAssignedDriverId());
        assertEquals("Mateo Rossi", readyOrder.getAssignedDriverName());
        assertEquals(DriverStatus.ON_DELIVERY, driver.getStatus());
        assertEquals(orderId, driver.getCurrentOrderId());

        Order deliveredOrder = orderService.updateStatus(orderId, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, deliveredOrder.getStatus());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
        assertNull(driver.getCurrentOrderId());
        assertEquals(1, driver.getCompletedDeliveries());
    }
}
