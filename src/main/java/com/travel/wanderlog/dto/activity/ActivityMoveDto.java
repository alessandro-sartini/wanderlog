package com.travel.wanderlog.dto.activity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ActivityMoveDto(
    @NotNull Long toDayId,
    @Min(1) Integer targetOrder 
) {}
