package com.travel.wanderlog.dto.trip;

import java.time.LocalDate;

// Patch: tutti opzionali. Valideremo coerenza date in service.
public record TripUpdateDto(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        VisibilityDto visibility) {
}