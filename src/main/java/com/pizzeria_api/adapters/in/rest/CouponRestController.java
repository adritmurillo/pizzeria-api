package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.adapters.in.rest.dto.CouponResponse;
import com.pizzeria_api.ports.in.GetCouponsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponRestController {

    private final GetCouponsUseCase getCouponsUseCase;

    public CouponRestController(GetCouponsUseCase getCouponsUseCase) {
        this.getCouponsUseCase = getCouponsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> response = getCouponsUseCase.getAllCoupons().stream()
                .map(CouponResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
}
