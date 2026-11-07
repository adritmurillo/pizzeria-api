package com.pizzeria_api.domain.model;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
    public PagedResult{
        content = (content == null) ? List.of() : List.copyOf(content);
    }
}
