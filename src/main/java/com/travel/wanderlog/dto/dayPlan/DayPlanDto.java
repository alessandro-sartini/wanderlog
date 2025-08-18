package com.travel.wanderlog.dto.dayPlan;

import java.time.LocalDate;

public record DayPlanDto(
        Long id,
        Long tripId,
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
