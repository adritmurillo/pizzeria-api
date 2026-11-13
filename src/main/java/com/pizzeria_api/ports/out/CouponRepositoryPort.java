package com.pizzeria_api.ports.out;

import com.pizzeria_api.domain.model.Coupon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepositoryPort {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
    Optional<Coupon> findByCode(String code);
    List<Coupon> findAll();
}
