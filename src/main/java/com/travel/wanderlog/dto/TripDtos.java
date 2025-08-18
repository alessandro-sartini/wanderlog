// package com.travel.wanderlog.dto;


// import jakarta.validation.constraints.*; // Validation base
// import java.time.LocalDate;

// public record TripCreateDto(
//         @NotBlank String title,
//         String description,
//         LocalDate startDate,
//         LocalDate endDate,
//         @NotNull VisibilityDto visibility
// ) {}

// public record TripDto(
//         Long id,
//         Long ownerId,
//         String title,
//         String description,
//         LocalDate startDate,
//         LocalDate endDate,
//         VisibilityDto visibility
// ) {}

// public enum VisibilityDto { PUBLIC, PRIVATE }

