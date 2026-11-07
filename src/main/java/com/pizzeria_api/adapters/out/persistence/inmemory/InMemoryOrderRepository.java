package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatus;
import com.pizzeria_api.domain.model.PagedResult;
import com.pizzeria_api.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOrderRepository implements OrderRepositoryPort {
    private final Map<UUID, Order> ordersDatabase = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        ordersDatabase.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.ofNullable(ordersDatabase.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(ordersDatabase.values());
    }

    @Override
    public PagedResult<Order> findOrders(OrderStatus status, int page, int size) {
        List<Order> filtered = ordersDatabase.values().stream()
                .filter(o -> status == null || o.getStatus() == status)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .skip((long) page * size)
                .limit(size)
                .toList();

        long total = ordersDatabase.values().stream()
                .filter(o -> status == null || o.getStatus() == status)
                .count();

        int totalPages = (size > 0) ? (int) Math.ceil((double) total / size) : 0;
        boolean isFirst = page == 0;
        boolean isLast = totalPages == 0 || page >= totalPages - 1;

        return new PagedResult<>(
                filtered,
                page,
                size,
                total,
                totalPages,
                isFirst,
                isLast
        );
    }
}
