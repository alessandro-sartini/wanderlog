package com.travel.wanderlog.dto.activity;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public record ActivityCreateDto(
        @NotBlank String name,
        String description,
        LocalTime startTime,
        @Positive(message = "durationMinutes deve essere > 0") Integer durationMinutes,
        String placeName,
        String placeAddress,
        String placePlaceId,
        Double placeLatitude,
        Double placeLongitude,
        @Min(1) Integer orderInDay) {
}
