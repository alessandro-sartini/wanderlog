package com.travel.wanderlog.dto.dayPlan;

import java.time.LocalDate;

/** Tutti opzionali: i null NON sovrascrivono (grazie al mapper PATCH) */
public record DayPlanUpdateDto(
        Integer indexInTrip,
        LocalDate date,
        String title,
        String note,
        String mainPlaceName,
        String mainPlaceAddress,
        String mainPlacePlaceId,
        Double mainPlaceLatitude,
        Double mainPlaceLongitude) {
}