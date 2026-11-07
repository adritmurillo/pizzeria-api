package com.pizzeria_api.ports.out;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.domain.model.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    PagedResult<Order> findOrders(OrderStatus status, int page, int size);
}
