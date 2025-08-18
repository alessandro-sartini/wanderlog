package com.travel.wanderlog.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Body della PATCH /reorder.
 * targetOrder = posizione di destinazione (1..N)
 */
public record ActivityReorderDto(
        @NotNull @Min(1) Integer targetOrder) {
}
