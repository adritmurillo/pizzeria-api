package com.pizzeria_api.adapters.out.persistence.jpa.adapter;


import com.pizzeria_api.adapters.out.persistence.jpa.entity.PizzaJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataPizzaRepository;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class PizzaPersistenceAdapter implements PizzaRepositoryPort {

    private final SpringDataPizzaRepository springDataPizzaRepository;

    public PizzaPersistenceAdapter(SpringDataPizzaRepository springDataPizzaRepository){
        this.springDataPizzaRepository = springDataPizzaRepository;
    }


    @Override
    public Pizza save(Pizza pizza) {
        PizzaJpaEntity entity = toEntity(pizza);
        PizzaJpaEntity savedEntity = springDataPizzaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Pizza> findById(UUID id) {
        return springDataPizzaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pizza> findAll() {
        return springDataPizzaRepository.findAll().stream().map(this :: toDomain).toList();
    }

    private PizzaJpaEntity toEntity(Pizza domain) {
        return new PizzaJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getSize(),
                domain.getCategory(),
                domain.isAvailable()
        );
    }

    private Pizza toDomain(PizzaJpaEntity entity) {
        return new Pizza(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getSize(),
                entity.getCategory()
        );
    }
}
