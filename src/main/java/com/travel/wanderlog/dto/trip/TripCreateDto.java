package com.travel.wanderlog.dto.trip;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TripCreateDto(
    @NotBlank String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    @NotNull VisibilityDto visibility,
    @Min(1) Integer orderInOwner
) {}