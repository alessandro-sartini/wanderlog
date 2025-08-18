package com.travel.wanderlog.mapper;


import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.travel.wanderlog.dto.dayPlan.DayPlanShowDto;
import com.travel.wanderlog.model.Activity;
import com.travel.wanderlog.model.DayPlan;

@Mapper(
    componentModel = "spring",
    uses = { ActivityMapper.class },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DayPlanViewMapper {

    @Mapping(target = "tripId", source = "dp.trip.id")
    @Mapping(target = "activities", source = "activities") // convertite via ActivityMapper
    DayPlanShowDto toShowDto(DayPlan dp, List<Activity> activities);
}
