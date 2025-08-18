package com.travel.wanderlog.dto.trip.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TripCreateDto(
    @NotBlank String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    @NotNull VisibilityDto visibility
) {}