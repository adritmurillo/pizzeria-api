package com.pizzeria_api.adapters.in.rest.dto;

import com.pizzeria_api.domain.model.Coupon;
import com.pizzeria_api.domain.model.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        int minPizzaCount,
        int maxUses,
        int currentUses,
        LocalDateTime validUntil,
        boolean active
) {
    public static CouponResponse fromDomain(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderAmount(),
                coupon.getMinPizzaCount(),
                coupon.getMaxUses(),
                coupon.getCurrentUses(),
                coupon.getValidUntil(),
                coupon.isActive()
        );
    }
}
