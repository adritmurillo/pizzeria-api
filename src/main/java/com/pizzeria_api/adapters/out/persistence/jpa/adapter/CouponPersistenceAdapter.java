package com.pizzeria_api.adapters.out.persistence.jpa.adapter;

import com.pizzeria_api.adapters.out.persistence.jpa.entity.CouponJpaEntity;
import com.pizzeria_api.adapters.out.persistence.jpa.repository.SpringDataCouponRepository;
import com.pizzeria_api.domain.model.Coupon;
import com.pizzeria_api.ports.out.CouponRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CouponPersistenceAdapter implements CouponRepositoryPort {

    private final SpringDataCouponRepository springDataCouponRepository;

    public CouponPersistenceAdapter(SpringDataCouponRepository springDataCouponRepository) {
        this.springDataCouponRepository = springDataCouponRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        Optional<CouponJpaEntity> existingOpt = springDataCouponRepository.findById(coupon.getId());

        CouponJpaEntity entityToSave;
        if (existingOpt.isPresent()) {
            entityToSave = existingOpt.get();
            entityToSave.setCode(coupon.getCode());
            entityToSave.setDiscountType(coupon.getDiscountType());
            entityToSave.setDiscountValue(coupon.getDiscountValue());
            entityToSave.setMinOrderAmount(coupon.getMinOrderAmount());
            entityToSave.setMinPizzaCount(coupon.getMinPizzaCount());
            entityToSave.setMaxUses(coupon.getMaxUses());
            entityToSave.setCurrentUses(coupon.getCurrentUses());
            entityToSave.setValidUntil(coupon.getValidUntil());
            entityToSave.setActive(coupon.isActive());
        } else {
            entityToSave = toEntity(coupon);
        }

        CouponJpaEntity savedEntity = springDataCouponRepository.save(entityToSave);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return springDataCouponRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return springDataCouponRepository.findByCode(code.trim().toUpperCase(Locale.ROOT)).map(this::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return springDataCouponRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private CouponJpaEntity toEntity(Coupon domain) {
        return new CouponJpaEntity(
                domain.getId(),
                domain.getCode(),
                domain.getDiscountType(),
                domain.getDiscountValue(),
                domain.getMinOrderAmount(),
                domain.getMinPizzaCount(),
                domain.getMaxUses(),
                domain.getCurrentUses(),
                domain.getValidUntil(),
                domain.isActive()
        );
    }

    private Coupon toDomain(CouponJpaEntity entity) {
        return new Coupon(
                entity.getId(),
                entity.getCode(),
                entity.getDiscountType(),
                entity.getDiscountValue(),
                entity.getMinOrderAmount(),
                entity.getMinPizzaCount(),
                entity.getMaxUses(),
                entity.getCurrentUses(),
                entity.getValidUntil(),
                entity.isActive()
        );
    }
}
