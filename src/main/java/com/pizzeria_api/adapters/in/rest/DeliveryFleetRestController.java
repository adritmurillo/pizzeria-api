package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.adapters.in.rest.dto.DeliveryDriverResponse;
import com.pizzeria_api.domain.model.DeliveryDriver;
import com.pizzeria_api.ports.in.GetDeliveryFleetUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DeliveryFleetRestController {

    private final GetDeliveryFleetUseCase getDeliveryFleetUseCase;

    public DeliveryFleetRestController(GetDeliveryFleetUseCase getDeliveryFleetUseCase) {
        this.getDeliveryFleetUseCase = getDeliveryFleetUseCase;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryDriverResponse>> getDrivers(
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly
    ) {
        List<DeliveryDriver> drivers = availableOnly
                ? getDeliveryFleetUseCase.getAvailableDrivers()
                : getDeliveryFleetUseCase.getAllDrivers();

        List<DeliveryDriverResponse> response = drivers.stream()
                .map(DeliveryDriverResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }
}
