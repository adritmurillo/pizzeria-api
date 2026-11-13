package com.pizzeria_api.domain.exception;

public class InvalidDriverException extends RuntimeException {
    public InvalidDriverException(String message) {
        super(message);
    }
}
