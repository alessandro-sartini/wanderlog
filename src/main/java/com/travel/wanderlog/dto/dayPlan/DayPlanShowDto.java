package com.travel.wanderlog.dto.dayPlan;


import java.time.LocalDate;
import java.util.List;
import com.travel.wanderlog.dto.activity.ActivityDto;

public record DayPlanShowDto(
    Long id,
    Long tripId,
    LocalDate date,
    Integer indexInTrip,
    String title,
    String note,
    String mainPlaceName,
    String mainPlaceAddress,
    String mainPlacePlaceId,
    Double mainPlaceLatitude,
    Double mainPlaceLongitude,
    List<ActivityDto> activities
) {}
