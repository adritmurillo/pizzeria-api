package com.pizzeria_api.domain;

import com.pizzeria_api.domain.exception.InvalidCouponException;
import com.pizzeria_api.domain.model.Coupon;
import com.pizzeria_api.domain.model.DiscountType;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.domain.specification.MinimumOrderAmountSpecification;
import com.pizzeria_api.domain.specification.MinimumPizzaCountSpecification;
import com.pizzeria_api.domain.specification.OrderSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CouponSpecificationTest {

    @Test
    @DisplayName("Should evaluate composite OrderSpecification (AND / OR / NOT) in O(1)")
    void shouldEvaluateCompositeOrderSpecifications() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Brava", 3, new BigDecimal("20.00")); // subtotal 60.00, 3 pizzas
        Order order = new Order(UUID.randomUUID(), "Joaquin", "Av. Principal 100", List.of(item));

        OrderSpecification minAmount45 = new MinimumOrderAmountSpecification(new BigDecimal("45.00"));
        OrderSpecification minPizzas3 = new MinimumPizzaCountSpecification(3);
        OrderSpecification minPizzas5 = new MinimumPizzaCountSpecification(5);

        assertTrue(minAmount45.and(minPizzas3).isSatisfiedBy(order));
        assertFalse(minAmount45.and(minPizzas5).isSatisfiedBy(order));
        assertTrue(minAmount45.or(minPizzas5).isSatisfiedBy(order));
        assertTrue(minPizzas5.not().isSatisfiedBy(order));
    }

    @Test
    @DisplayName("Should calculate percentage discount, increment uses, and deactivate when reaching maxUses")
    void shouldApplyPercentageDiscountAndUpdateOrderTotals() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Brava", 3, new BigDecimal("20.00")); // 60.00
        Order order = new Order(UUID.randomUUID(), "Joaquin", "Av. Principal 100", List.of(item));

        Coupon fiesta25 = new Coupon(
                UUID.randomUUID(),
                "fiesta25",
                DiscountType.PERCENTAGE,
                new BigDecimal("25.00"),
                new BigDecimal("45.00"),
                3,
                1,
                0,
                LocalDateTime.now().plusDays(5),
                true
        );

        BigDecimal discount = fiesta25.applyTo(order, LocalDateTime.now());
        order.applyDiscount(fiesta25.getCode(), discount);

        assertEquals("FIESTA25", order.getAppliedCouponCode());
        assertEquals(new BigDecimal("60.00"), order.getSubtotalAmount());
        assertEquals(new BigDecimal("15.00"), order.getDiscountAmount());
        assertEquals(new BigDecimal("45.00"), order.getTotalAmount());
        assertEquals(1, fiesta25.getCurrentUses());
        assertFalse(fiesta25.isActive());

        fiesta25.revertRedemption();
        assertEquals(0, fiesta25.getCurrentUses());
        assertTrue(fiesta25.isActive());
    }

    @Test
    @DisplayName("Should reject coupon when order does not satisfy minimum amount or pizza count specification")
    void shouldRejectCouponWhenSpecificationIsNotSatisfied() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Margarita", 1, new BigDecimal("18.00"));
        Order smallOrder = new Order(UUID.randomUUID(), "Joaquin", "Calle 1", List.of(item));

        Coupon coupon = new Coupon(
                UUID.randomUUID(),
                "WELCOME10",
                DiscountType.PERCENTAGE,
                new BigDecimal("10.00"),
                new BigDecimal("25.00"),
                2,
                100,
                0,
                LocalDateTime.now().plusDays(1),
                true
        );

        assertThrows(InvalidCouponException.class, () -> coupon.applyTo(smallOrder, LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should cap fixed discount at order subtotal to prevent negative total amount")
    void shouldCapFixedDiscountAtOrderSubtotal() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Mini Pizza", 1, new BigDecimal("12.00"));
        Order order = new Order(UUID.randomUUID(), "Joaquin", "Calle 2", List.of(item));

        Coupon hugeFixedCoupon = new Coupon(
                UUID.randomUUID(),
                "GIFT20",
                DiscountType.FIXED_AMOUNT,
                new BigDecimal("20.00"),
                new BigDecimal("10.00"),
                1,
                10,
                0,
                LocalDateTime.now().plusDays(1),
                true
        );

        BigDecimal discount = hugeFixedCoupon.applyTo(order, LocalDateTime.now());
        order.applyDiscount(hugeFixedCoupon.getCode(), discount);

        assertEquals(new BigDecimal("12.00"), order.getDiscountAmount());
        assertEquals(new BigDecimal("0.00"), order.getTotalAmount());
    }
}
