package com.travel.wanderlog.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int page, // indice corrente (0-based, o 1-based se abiliti l’opzione)
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        String next, // URL pagina successiva (o null)
        String prev // URL pagina precedente (o null)
) {
}