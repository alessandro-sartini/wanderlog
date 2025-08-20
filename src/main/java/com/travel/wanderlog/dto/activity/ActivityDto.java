package com.travel.wanderlog.dto.activity;

import java.time.LocalTime;

public record ActivityDto(
  Long id,
  Long dayPlanId,
  String name,
  String description,
  LocalTime startTime,
  Integer durationMinutes,
  String placeName,
  String placeAddress,
  String placeId,
  Double placeLatitude,
  Double placeLongitude,
  Integer orderInDay

  
) {}
