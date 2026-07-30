package com.huellitasoaxaca.backend.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record PaginaResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
)
{
    public static <T> PaginaResponse<T> desde(Page<T> page)
    {
        return new PaginaResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
