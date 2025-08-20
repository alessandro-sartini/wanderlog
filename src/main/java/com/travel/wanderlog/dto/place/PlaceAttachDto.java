package com.travel.wanderlog.dto.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlaceAttachDto(
    @NotBlank String provider,          // "nominatim" (o "google" domani)
    @NotBlank String providerPlaceId,   // es. "relation:1834818"
    @NotBlank String name,              // es. "Colosseo"
    String formattedAddress,            // indirizzo formattato
    @NotNull Double lat,
    @NotNull Double lon
) {}
