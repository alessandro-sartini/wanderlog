package com.travel.wanderlog.dto.trip;

import java.time.LocalDate;
import java.util.List;

import com.travel.wanderlog.dto.dayPlan.DayPlanSummaryDto;

public record TripShowDto(
    Long id,
    Long ownerId,
    String title,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    VisibilityDto visibility,
    List<DayPlanSummaryDto> days
) {}
