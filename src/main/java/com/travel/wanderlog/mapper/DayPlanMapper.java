package com.travel.wanderlog.mapper;


import com.travel.wanderlog.dto.dayPlan.DayPlanCreateDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanUpdateDto;
import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DayPlanMapper {

  @Mappings({
    @Mapping(target = "id", ignore = true),
    @Mapping(target = "trip", source = "trip"),
    @Mapping(target = "indexInTrip", source = "dto.indexInTrip"),
    @Mapping(target = "date", source = "dto.date"),
    @Mapping(target = "title", source = "dto.title"),
    @Mapping(target = "note", source = "dto.note"),
    @Mapping(target = "mainPlaceName", source = "dto.mainPlaceName"),
    @Mapping(target = "mainPlaceAddress", source = "dto.mainPlaceAddress"),
    @Mapping(target = "mainPlacePlaceId", source = "dto.mainPlacePlaceId"),
    @Mapping(target = "mainPlaceLatitude", source = "dto.mainPlaceLatitude"),
    @Mapping(target = "mainPlaceLongitude", source = "dto.mainPlaceLongitude")
  })
  DayPlan toEntity(DayPlanCreateDto dto, Trip trip);

  @Mapping(target = "tripId", source = "trip.id")
  DayPlanDto toDto(DayPlan entity);

  /** PATCH: ignora i null, così aggiorniamo solo i campi passati */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromDto(DayPlanUpdateDto dto, @MappingTarget DayPlan target);
}
