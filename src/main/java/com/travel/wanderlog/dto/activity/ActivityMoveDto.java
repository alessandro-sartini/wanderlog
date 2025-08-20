package com.travel.wanderlog.dto.activity;

import org.hibernate.id.IntegralDataTypeHolder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ActivityMoveDto(
    @NotNull Long toDayId,
    @Min(1) Integer targetOrder 
) {}
