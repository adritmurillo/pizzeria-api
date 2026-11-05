package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.Order;
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
}
