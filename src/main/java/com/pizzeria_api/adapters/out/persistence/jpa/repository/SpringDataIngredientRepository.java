package com.pizzeria_api.adapters.out.persistence.jpa.repository;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.IngredientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataIngredientRepository extends JpaRepository<IngredientJpaEntity, UUID> {
}
