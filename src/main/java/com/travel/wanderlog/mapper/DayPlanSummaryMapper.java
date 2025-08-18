package com.travel.wanderlog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.travel.wanderlog.dto.dayPlan.DayPlanSummaryDto;
import com.travel.wanderlog.model.DayPlan;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DayPlanSummaryMapper {

    @Mapping(target = "id", source = "trip.id")
    DayPlanSummaryDto toSummary(DayPlan dp);
}