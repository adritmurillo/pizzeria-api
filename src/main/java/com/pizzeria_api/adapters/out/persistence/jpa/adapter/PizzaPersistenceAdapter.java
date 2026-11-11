package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.PizzaJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataPizzaRepository;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.domain.model.RecipeItem;
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

    public PizzaPersistenceAdapter(SpringDataPizzaRepository springDataPizzaRepository) {
        this.springDataPizzaRepository = springDataPizzaRepository;
    }

    @Override
    public Pizza save(Pizza pizza) {
        Optional<PizzaJpaEntity> existingOpt = springDataPizzaRepository.findById(pizza.getId());

        PizzaJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            entityToSave = existingOpt.get();
            entityToSave.setName(pizza.getName());
            entityToSave.setDescription(pizza.getDescription());
            entityToSave.setPrice(pizza.getPrice());
            entityToSave.setSize(pizza.getSize());
            entityToSave.setCategory(pizza.getCategory());
            entityToSave.setAvailable(pizza.isAvailable());
            entityToSave.setStock(pizza.getStock());
        } else {
            entityToSave = toEntity(pizza);
        }

        PizzaJpaEntity savedEntity = springDataPizzaRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Pizza> findById(UUID id) {
        return springDataPizzaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pizza> findAll() {
        return springDataPizzaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private PizzaJpaEntity toEntity(Pizza domain) {
        return new PizzaJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getSize(),
                domain.getCategory(),
                domain.isAvailable(),
                domain.getStock()
        );
    }

    private Pizza toDomain(PizzaJpaEntity entity) {
        List<RecipeItem> domainRecipe = (entity.getRecipe() == null) ? List.of() :
                entity.getRecipe().stream()
                        .map(r -> new RecipeItem(
                                r.getIngredient().getId(),
                                r.getIngredient().getName(),
                                r.getQuantityRequired(),
                                r.getUnit()
                        ))
                        .toList();

        return new Pizza(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getSize(),
                entity.getCategory(),
                entity.isAvailable(),
                entity.getStock(),
                domainRecipe
        );
    }
}
