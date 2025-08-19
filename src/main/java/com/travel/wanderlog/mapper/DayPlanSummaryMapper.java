package com.travel.wanderlog.mapper;

import com.travel.wanderlog.dto.dayPlan.DayPlanSummaryDto;
import com.travel.wanderlog.model.DayPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DayPlanSummaryMapper {

    // prendi l'id del DayPlan, NON del Trip
    @Mapping(target = "id", source = "id")
    @Mapping(target = "indexInTrip", source = "indexInTrip")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "title", source = "title")
    // se vuoi calcolare le attività, lascia pure null o aggiungi un count dedicato
    // @Mapping(target = "activitiesCount", expression = "java(dp.getActivities() != null ? dp.getActivities().size() : null)")
    DayPlanSummaryDto toDto(DayPlan dp);
}
