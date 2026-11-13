package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        String deliveryAddress,
        List<OrderItemResponse> items,
        BigDecimal subtotalAmount,
        String appliedCouponCode,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        OrderStatus status,
        int estimatedWaitMinutes,
        LocalDateTime estimatedReadyAt,
        UUID assignedDriverId,
        String assignedDriverName,
        LocalDateTime createdAt
) {
    public record OrderItemResponse(
            UUID pizzaId,
            String pizzaName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ){
        public static OrderItemResponse fromDomain(OrderItem item){
            return new OrderItemResponse(
                    item.getPizzaId(),
                    item.getPizzaName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.calculateSubtotal()
            );
        }
    }

    public static OrderResponse fromDomain(Order order){
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(OrderItemResponse :: fromDomain)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getDeliveryAddress(),
                itemResponses,
                order.getSubtotalAmount(),
                order.getAppliedCouponCode(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getEstimatedWaitMinutes(),
                order.getEstimatedReadyAt(),
                order.getAssignedDriverId(),
                order.getAssignedDriverName(),
                order.getCreatedAt()
        );
    }
}
