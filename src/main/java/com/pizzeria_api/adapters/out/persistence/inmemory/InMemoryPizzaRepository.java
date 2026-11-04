package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPizzaRepository implements PizzaRepositoryPort {
    private final Map<UUID, Pizza> database = new ConcurrentHashMap<>();

    @Override
    public Pizza save(Pizza pizza) {
        database.put(pizza.getId(), pizza);
        return pizza;
    }

    @Override
    public Optional<Pizza> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Pizza> findAll() {
        return new ArrayList<>(database.values());
    }
}
