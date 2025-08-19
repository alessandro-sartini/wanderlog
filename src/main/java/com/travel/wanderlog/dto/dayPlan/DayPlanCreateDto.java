package com.travel.wanderlog.dto.dayPlan;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;

public record DayPlanCreateDto(
                @Min(1) Integer indexInTrip,
                LocalDate date,
                String title,
                String note,
                String mainPlaceName,
                String mainPlaceAddress,
                String mainPlacePlaceId,
                Double mainPlaceLatitude,
                Double mainPlaceLongitude) {
}