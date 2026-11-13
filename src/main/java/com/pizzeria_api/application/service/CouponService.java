package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.model.Coupon;
import com.pizzeria_api.ports.in.GetCouponsUseCase;
import com.pizzeria_api.ports.out.CouponRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CouponService implements GetCouponsUseCase {

    private final CouponRepositoryPort couponRepositoryPort;

    public CouponService(CouponRepositoryPort couponRepositoryPort) {
        this.couponRepositoryPort = couponRepositoryPort;
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepositoryPort.findAll();
    }
}
