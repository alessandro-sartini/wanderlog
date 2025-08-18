package com.travel.wanderlog.dto.dayPlan;

import java.time.LocalDate;

public record DayPlanSummaryDto(
        Long id,
        Integer indexInTrip,
        LocalDate date,
        String title,
        Integer activitiesCount) {
}
