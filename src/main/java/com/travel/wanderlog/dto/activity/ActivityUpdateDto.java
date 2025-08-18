package com.travel.wanderlog.dto.activity;

import java.time.LocalTime;

/** Tutti opzionali; i null NON sovrascrivono (patch) */
public record ActivityUpdateDto(
        String name,
        String description,
        LocalTime startTime,
        Integer durationMinutes,
        String placeName,
        String placeAddress,
        String placePlaceId,
        Double placeLatitude,
        Double placeLongitude,
        Integer orderInDay) {
}