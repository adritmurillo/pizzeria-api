package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Pizza;

import java.util.List;
import java.util.UUID;

public interface GetPizzaCatalogUseCase {
    List<Pizza> getCatalog();
    Pizza getPizzaById(UUID id);
}
