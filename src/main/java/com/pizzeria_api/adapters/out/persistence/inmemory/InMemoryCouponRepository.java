package com.pizzeria_api.adapters.out.persistence.inmemory;

import com.pizzeria_api.domain.model.Coupon;
import com.pizzeria_api.ports.out.CouponRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCouponRepository implements CouponRepositoryPort {

    private final Map<UUID, Coupon> storage = new ConcurrentHashMap<>();

    @Override
    public Coupon save(Coupon coupon) {
        storage.put(coupon.getId(), coupon);
        return coupon;
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim().toUpperCase(Locale.ROOT);
        return storage.values().stream()
                .filter(c -> c.getCode().equals(normalized))
                .findFirst();
    }

    @Override
    public List<Coupon> findAll() {
        return List.copyOf(storage.values());
    }
}
