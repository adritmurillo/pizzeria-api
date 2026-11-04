package com.pizzeria_api.domain.exception;

public class InvalidPizzaException extends RuntimeException {
    public InvalidPizzaException(String message) {
        super(message);
    }
}
