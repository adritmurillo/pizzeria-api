package com.pizzeria_api.ports.out;

import com.pizzeria_api.domain.model.Pizza;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PizzaRepositoryPort {
    Pizza save(Pizza pizza);
    Optional<Pizza> findById(UUID id);
    List<Pizza> findAll();

}
