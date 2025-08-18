package com.travel.wanderlog.mapper;


import com.travel.wanderlog.dto.activity.ActivityCreateDto;
import com.travel.wanderlog.dto.activity.ActivityDto;
import com.travel.wanderlog.dto.activity.ActivityUpdateDto;
import com.travel.wanderlog.model.Activity;
import com.travel.wanderlog.model.DayPlan;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

  @Mappings({
    @Mapping(target = "id", ignore = true),
    @Mapping(target = "createdAt", ignore = true),
    @Mapping(target = "updatedAt", ignore = true),
    @Mapping(target = "dayPlan", source = "dayPlan")
  })
  Activity toEntity(ActivityCreateDto dto, DayPlan dayPlan);

  @Mapping(target = "dayPlanId", source = "dayPlan.id")
  ActivityDto toDto(Activity entity);

  // PATCH: ignora i null
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateFromDto(ActivityUpdateDto dto, @MappingTarget Activity target);
}