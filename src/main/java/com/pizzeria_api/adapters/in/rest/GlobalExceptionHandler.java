package com.pizzeria_api.adapters.in.rest;

import com.pizzeria_api.domain.exception.InvalidOrderException;
import com.pizzeria_api.domain.exception.InvalidPizzaException;
import com.pizzeria_api.domain.exception.OrderNotFoundException;
import com.pizzeria_api.domain.exception.PizzaNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PizzaNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidPizzaException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPizza(InvalidPizzaException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrder(InvalidOrderException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Validation Failed");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f -> fieldErrors.put(f.getField(), f.getDefaultMessage()));
        error.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Malformed JSON or Invalid Field Value");

        InvalidFormatException ife = findCause(ex, InvalidFormatException.class);

        if (ife != null && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String acceptedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String fieldName = "field";
            if (!ife.getPath().isEmpty() && ife.getPath().getFirst().getPropertyName() != null) {
                fieldName = ife.getPath().getFirst().getPropertyName();
            }

            error.put("message", String.format("Invalid value '%s' for field '%s'. Accepted values are: [%s]",
                    ife.getValue(), fieldName, acceptedValues));
        } else {
            error.put("message", "Request body is invalid or malformed JSON");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @SuppressWarnings("unchecked")
    private <T extends Throwable> T findCause(Throwable throwable, Class<T> targetClass) {
        Throwable current = throwable;
        while (current != null) {
            if (targetClass.isInstance(current)) {
                return (T) current;
            }
            current = current.getCause();
        }
        return null;
    }
}
