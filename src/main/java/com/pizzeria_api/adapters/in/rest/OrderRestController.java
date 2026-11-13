package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.adapters.in.rest.dto.CreateOrderRequest;
import com.pizzeria_api.adapters.in.rest.dto.OrderResponse;
import com.pizzeria_api.adapters.in.rest.dto.UpdateOrderStatusRequest;
import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.domain.model.PagedResult;
import com.pizzeria_api.ports.in.CreateOrderUseCase;
import com.pizzeria_api.ports.in.DispatchOrderUseCase;
import com.pizzeria_api.ports.in.GetOrderUseCase;
import com.pizzeria_api.ports.in.GetOrdersUseCase;
import com.pizzeria_api.ports.in.UpdateOrderStatusUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final DispatchOrderUseCase dispatchOrderUseCase;

    public OrderRestController(CreateOrderUseCase createOrderUseCase,
                               GetOrderUseCase getOrderUseCase,
                               GetOrdersUseCase getOrdersUseCase,
                               UpdateOrderStatusUseCase updateOrderStatusUseCase,
                               DispatchOrderUseCase dispatchOrderUseCase){
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.getOrdersUseCase = getOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.dispatchOrderUseCase = dispatchOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request
            ){
        List<CreateOrderUseCase.OrderItemCommand> itemCommands = request.items().stream()
                .map(item -> new CreateOrderUseCase.OrderItemCommand(
                        item.pizzaId(),
                        item.quantity()
                )).toList();

        CreateOrderUseCase.CreateOrderCommand command = new CreateOrderUseCase.CreateOrderCommand(
                request.customerName(),
                request.deliveryAddress(),
                itemCommands,
                request.couponCode()
        );

        Order savedOrder = createOrderUseCase.createOrder(command, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromDomain(savedOrder));
    }

    @GetMapping
    public ResponseEntity<PagedResult<OrderResponse>> getOrders(
            @RequestParam(required = false)OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        PagedResult<Order> domainResult = getOrdersUseCase.getOrders(status, page, size);
        List<OrderResponse> responseContent = domainResult.content().stream().map(OrderResponse :: fromDomain).toList();

        PagedResult<OrderResponse> response = new PagedResult<>(
                responseContent,
                domainResult.pageNumber(),
                domainResult.pageSize(),
                domainResult.totalElements(),
                domainResult.totalPages(),
                domainResult.isFirst(),
                domainResult.isLast()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id){
        Order order = getOrderUseCase.getOrderById(id);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID id,
                                                           @Valid @RequestBody UpdateOrderStatusRequest request){
        Order updatedOrder = updateOrderStatusUseCase.updateStatus(id, request.status());
        return ResponseEntity.ok(OrderResponse.fromDomain(updatedOrder));

    }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<OrderResponse> dispatchOrder(@PathVariable UUID id) {
        Order dispatchedOrder = dispatchOrderUseCase.dispatchOrder(id);
        return ResponseEntity.ok(OrderResponse.fromDomain(dispatchedOrder));
    }
}
