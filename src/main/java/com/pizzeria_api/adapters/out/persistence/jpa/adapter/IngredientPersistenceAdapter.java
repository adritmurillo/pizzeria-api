package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.IngredientJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataIngredientRepository;
import com.pizzeria_api.domain.model.Ingredient;
import com.pizzeria_api.ports.out.IngredientRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class IngredientPersistenceAdapter implements IngredientRepositoryPort {

    private final SpringDataIngredientRepository springDataIngredientRepository;

    public IngredientPersistenceAdapter(SpringDataIngredientRepository springDataIngredientRepository) {
        this.springDataIngredientRepository = springDataIngredientRepository;
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        Optional<IngredientJpaEntity> existingOpt = springDataIngredientRepository.findById(ingredient.getId());

        IngredientJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            entityToSave = existingOpt.get();
            entityToSave.setName(ingredient.getName());
            entityToSave.setUnit(ingredient.getUnit());
            entityToSave.setStock(ingredient.getStock());
            entityToSave.setCostPerUnit(ingredient.getCostPerUnit());
        } else {
            entityToSave = toEntity(ingredient);
        }

        IngredientJpaEntity savedEntity = springDataIngredientRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Ingredient> findById(UUID id) {
        return springDataIngredientRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ingredient> findAll() {
        return springDataIngredientRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private IngredientJpaEntity toEntity(Ingredient domain) {
        return new IngredientJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getUnit(),
                domain.getStock(),
                domain.getCostPerUnit()
        );
    }

    private Ingredient toDomain(IngredientJpaEntity entity) {
        return new Ingredient(
                entity.getId(),
                entity.getName(),
                entity.getUnit(),
                entity.getStock(),
                entity.getCostPerUnit()
        );
    }
}
