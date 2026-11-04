package com.pizzeria_api.application.service;

import com.pizzeria_api.domain.exception.PizzaNotFoundException;
import com.pizzeria_api.domain.model.Pizza;
import com.pizzeria_api.domain.model.PizzaCategory;
import com.pizzeria_api.domain.model.PizzaSize;
import com.pizzeria_api.ports.out.PizzaRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceTest {
    @Mock
    private PizzaRepositoryPort pizzaRepositoryPort;

    @InjectMocks
    private PizzaService pizzaService;

    @Test
    @DisplayName("Should return pizza when exists by ID")
    void shouldReturnPizzaWhenExists(){
        UUID id = UUID.randomUUID();
        Pizza pizza = new Pizza(id, "Pepperoni", "Spicy", new BigDecimal("15.00"),
                PizzaSize.LARGE, PizzaCategory.CLASSIC);
        when(pizzaRepositoryPort.findById(id)).thenReturn(Optional.of(pizza));
        Pizza result = pizzaService.getPizzaById(id);
        assertNotNull(result);
        assertEquals("Pepperoni", result.getName());
        verify(pizzaRepositoryPort, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should throw PizzaNotFoundException when pizza does not exist")
    void shouldThrowExceptionWhenNotFound(){
        UUID id = UUID.randomUUID();
        when(pizzaRepositoryPort.findById(id)).thenReturn(Optional.empty());
        assertThrows(PizzaNotFoundException.class, () ->
                pizzaService.getPizzaById(id));
        verify(pizzaRepositoryPort, times(1)).findById(id);
    }
}
