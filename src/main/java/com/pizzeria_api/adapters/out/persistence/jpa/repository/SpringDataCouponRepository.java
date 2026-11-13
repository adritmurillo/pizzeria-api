package com.pizzeria_api.adapters.out.persistence.jpa.repository;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.CouponJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataCouponRepository extends JpaRepository<CouponJpaEntity, UUID> {
    Optional<CouponJpaEntity> findByCode(String code);
}
