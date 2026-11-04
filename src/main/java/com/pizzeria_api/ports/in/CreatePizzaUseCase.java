package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Pizza;

public interface CreatePizzaUseCase {
    Pizza createPizza(Pizza pizza);
}
