package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order createSamplePendingOrder() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Margherita", 2, new BigDecimal("12.50"));
        return new Order(UUID.randomUUID(), "Carlos Test", "Av. Principal 123", List.of(item));
    }

    @Test
    @DisplayName("Should cancel a pending order successfully")
    void shouldCancelPendingOrder() {
        Order order = createSamplePendingOrder();
        assertEquals(OrderStatus.PENDING, order.getStatus());

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidOrderException when cancelling an already cancelled order")
    void shouldFailWhenCancellingAlreadyCancelledOrder() {
        Order order = createSamplePendingOrder();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        InvalidOrderException exception = assertThrows(InvalidOrderException.class, order::cancel);
        assertEquals("Order is already cancelled", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidOrderException when cancelling a delivered order")
    void shouldFailWhenCancellingDeliveredOrder() {
        Order order = createSamplePendingOrder();
        order.markAsPreparing();
        order.markAsReady();
        order.markAsDelivered();
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        InvalidOrderException exception = assertThrows(InvalidOrderException.class, order::cancel);
        assertEquals("Cannot cancel an order that has already been delivered", exception.getMessage());
    }
}
