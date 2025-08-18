package com.travel.wanderlog.dto.trip.dto;

import java.time.LocalDate;

public record TripDto(
    Long id,
    Long ownerId,
    String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    VisibilityDto visibility
) {}
