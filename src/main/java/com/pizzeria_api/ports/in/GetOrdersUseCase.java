package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.domain.model.PagedResult;

public interface GetOrdersUseCase {
    PagedResult<Order> getOrders(OrderStatus status, int page, int size);
}
