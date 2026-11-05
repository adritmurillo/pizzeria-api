package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.adapters.in.rest.dto.CreateOrderRequest;
import com.pizzeria_api.adapters.in.rest.dto.OrderResponse;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderItem;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final CreateOrderUseCase createOrderUseCase;

    public OrderRestController(CreateOrderUseCase createOrderUseCase){
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request
            ){
        List<OrderItem> domainItems = request.items().stream()
                .map(item -> new OrderItem(
                        item.pizzaId(),
                        item.pizzaName(),
                        item.quantity(),
                        item.unitPrice()
                ))
                .toList();
        Order orderToCreate = new Order(
                null,
                request.customerName(),
                request.deliveryAddress(),
                domainItems
        );

        Order savedOrder = createOrderUseCase.createOrder(orderToCreate, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromDomain(savedOrder));
    }
}
