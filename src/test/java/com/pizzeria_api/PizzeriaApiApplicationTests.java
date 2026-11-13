package com.pizzeria_api;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.in.GetCouponsUseCase;
import com.pizzeria_api.ports.in.GetDeliveryFleetUseCase;
import com.pizzeria_api.ports.in.UpdateOrderStatusUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PizzeriaApiApplicationTests {

	@Autowired
	private CreateOrderUseCase createOrderUseCase;

	@Autowired
	private UpdateOrderStatusUseCase updateOrderStatusUseCase;

	@Autowired
	private GetCouponsUseCase getCouponsUseCase;

	@Autowired
	private GetDeliveryFleetUseCase getDeliveryFleetUseCase;

	@Test
	void contextLoads() {
		assertFalse(getCouponsUseCase.getAllCoupons().isEmpty());
		assertFalse(getDeliveryFleetUseCase.getAllDrivers().isEmpty());
	}

	@Test
	@DisplayName("Integration: Full Food-Tech Order Lifecycle (BOM + Coupon Specification + Fleet Dispatcher) in PostgreSQL")
	void shouldCreateOrderWithCouponAndBomAndDispatchDriverInPostgreSql() {
		UUID pizzaBravaId = UUID.fromString("c0825f38-bced-40dc-a8a6-ad0db4387ff3");

		CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
				"Joaquin Integration",
				"Av. Arequipa 1200",
				List.of(new CreateOrderUseCase.OrderItemCommand(pizzaBravaId, 1)),
				"WELCOME10"
		);

		Order savedOrder = createOrderUseCase.createOrder(command, "integration-key-peldano-15-16");

		assertNotNull(savedOrder.getId());
		assertEquals(new BigDecimal("35.00"), savedOrder.getSubtotalAmount());
		assertEquals("WELCOME10", savedOrder.getAppliedCouponCode());
		assertEquals(new BigDecimal("3.50"), savedOrder.getDiscountAmount());
		assertEquals(new BigDecimal("31.50"), savedOrder.getTotalAmount());

		Order preparingOrder = updateOrderStatusUseCase.updateStatus(savedOrder.getId(), OrderStatus.PREPARING);
		assertEquals(OrderStatus.PREPARING, preparingOrder.getStatus());

		Order readyOrder = updateOrderStatusUseCase.updateStatus(savedOrder.getId(), OrderStatus.READY);
		assertEquals(OrderStatus.READY, readyOrder.getStatus());
		assertNotNull(readyOrder.getAssignedDriverId());
		assertEquals("Mateo Rossi", readyOrder.getAssignedDriverName());

		Order deliveredOrder = updateOrderStatusUseCase.updateStatus(savedOrder.getId(), OrderStatus.DELIVERED);
		assertEquals(OrderStatus.DELIVERED, deliveredOrder.getStatus());
	}

}
