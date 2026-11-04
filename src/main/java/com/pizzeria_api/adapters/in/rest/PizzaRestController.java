package com.pizzeria_api.adapters.in.rest;


import com.pizzeria_api.adapters.in.rest.dto.CreatePizzaRequest;
import com.pizzeria_api.adapters.in.rest.dto.PizzaResponse;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.ports.in.CreatePizzaUseCase;
import com.pizzeria_api.ports.in.GetPizzaCatalogUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pizzas")
public class PizzaRestController {
    private final CreatePizzaUseCase createPizzaUseCase;
    private final GetPizzaCatalogUseCase getPizzaCatalogUseCase;

    public PizzaRestController(CreatePizzaUseCase createPizzaUseCase, GetPizzaCatalogUseCase getPizzaCatalogUseCase){
        this.createPizzaUseCase = createPizzaUseCase;
        this.getPizzaCatalogUseCase = getPizzaCatalogUseCase;
    }

    @PostMapping
    public ResponseEntity<PizzaResponse> createPizza(@Valid @RequestBody CreatePizzaRequest request){
        Pizza pizzaToCreate = new Pizza(
                null,
                request.name(),
                request.description(),
                request.price(),
                request.size(),
                request.category()
        );
        Pizza createdPizza = createPizzaUseCase.createPizza(pizzaToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(PizzaResponse.fromDomain(createdPizza));
    }

    @GetMapping
    public ResponseEntity<List<PizzaResponse>> getCatalog(){
        List<PizzaResponse> catalog = getPizzaCatalogUseCase.getCatalog().stream()
                .map(PizzaResponse :: fromDomain)
                .toList();
        return ResponseEntity.ok(catalog);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PizzaResponse> getPizzaById(@PathVariable UUID id){
        Pizza pizza = getPizzaCatalogUseCase.getPizzaById(id);
        return ResponseEntity.ok(PizzaResponse.fromDomain(pizza));
    }
}
