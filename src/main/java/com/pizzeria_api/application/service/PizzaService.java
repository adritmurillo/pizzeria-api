package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.exception.PizzaNotFoundException;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.ports.in.CreatePizzaUseCase;
import com.pizzeria_api.ports.in.GetPizzaCatalogUseCase;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PizzaService implements CreatePizzaUseCase, GetPizzaCatalogUseCase {
    private final PizzaRepositoryPort pizzaRepositoryPort;

    public PizzaService(PizzaRepositoryPort pizzaRepositoryPort){
        this.pizzaRepositoryPort = pizzaRepositoryPort;
    }

    @Override
    public Pizza createPizza(Pizza pizza) {
        return pizzaRepositoryPort.save(pizza);
    }

    @Override
    public List<Pizza> getCatalog() {
        return pizzaRepositoryPort.findAll();
    }

    @Override
    public Pizza getPizzaById(UUID id) {
        return pizzaRepositoryPort.findById(id).orElseThrow(
                () -> new PizzaNotFoundException("Pizza not found with id: " + id));
    }
}
