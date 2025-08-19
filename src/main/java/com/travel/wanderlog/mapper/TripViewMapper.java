package com.travel.wanderlog.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;

@Mapper(
    componentModel = "spring",
    uses = { DayPlanSummaryMapper.class }, // deve esistere già
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TripViewMapper {

    // Show "completo": trip + lista day plans passata dal service
    @Mapping(target = "ownerId", source = "trip.owner.id")
    @Mapping(target = "days",    source = "days")
    TripShowDto toShowDto(Trip trip, List<DayPlan> days);

    // (opzionale) Show "leggero" senza days, utile se vuoi solo i campi base
    @Mapping(target = "ownerId", source = "owner.id")
    TripShowDto toShowOnly(Trip trip);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TripUpdateDto dto, @MappingTarget Trip entity);
}
