package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.domain.model.Order;
import com.pizzeria_api.domain.model.OrderStatusChangedEvent;
import com.pizzeria_api.ports.in.GetOrderUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/orders")
public class OrderTrackingRestController {

    private final GetOrderUseCase getOrderUseCase;
    // Registro concurrente: una orden puede ser observada por 1 o más clientes/pantallas
    private final Map<UUID, List<SseEmitter>> emittersByOrder = new ConcurrentHashMap<>();

    public OrderTrackingRestController(GetOrderUseCase getOrderUseCase) {
        this.getOrderUseCase = getOrderUseCase;
    }

    @GetMapping(value = "/{id}/tracking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter trackOrder(@PathVariable UUID id) {
        // Timeout de 30 minutos (1800000 ms)
        SseEmitter emitter = new SseEmitter(1800000L);

        // Cláusulas de limpieza cuando el cliente cierra la conexión o expira el timeout
        emitter.onCompletion(() -> removeEmitter(id, emitter));
        emitter.onTimeout(() -> removeEmitter(id, emitter));
        emitter.onError(e -> removeEmitter(id, emitter));

        // Registramos el cliente en la lista activa
        emittersByOrder.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // Push inmediato del estado actual al conectarse
        try {
            Order currentOrder = getOrderUseCase.getOrderById(id);
            emitter.send(SseEmitter.event()
                    .name("order-status")
                    .data(new OrderStatusChangedEvent(
                            id,
                            currentOrder.getStatus(),
                            currentOrder.getEstimatedWaitMinutes(),
                            currentOrder.getEstimatedReadyAt()
                    )));
        } catch (IOException e) {
            removeEmitter(id, emitter);
        }

        return emitter;
    }

    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        List<SseEmitter> emitters = emittersByOrder.get(event.orderId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("order-status")
                        .data(event));
            } catch (IOException e) {
                removeEmitter(event.orderId(), emitter);
            }
        }
    }

    private void removeEmitter(UUID orderId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByOrder.get(orderId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersByOrder.remove(orderId);
            }
        }
    }
}
