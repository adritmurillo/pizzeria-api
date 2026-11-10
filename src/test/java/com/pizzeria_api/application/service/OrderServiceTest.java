package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.*;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private PizzaRepositoryPort pizzaRepositoryPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should restore pizza stock when order is cancelled")
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
}
