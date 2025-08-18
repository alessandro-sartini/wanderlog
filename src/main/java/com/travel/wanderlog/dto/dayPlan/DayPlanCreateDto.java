package com.travel.wanderlog.dto.dayPlan;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record DayPlanCreateDto(
        @NotNull Integer indexInTrip,
        LocalDate date,
        String title,
        String note,
        String mainPlaceName,
        String mainPlaceAddress,
        String mainPlacePlaceId,
        Double mainPlaceLatitude,
        Double mainPlaceLongitude) {
}