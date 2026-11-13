package com.pizzeria_api.ports.in;

import com.pizzeria_api.domain.model.Coupon;

import java.util.List;

public interface GetCouponsUseCase {
    List<Coupon> getAllCoupons();
}
