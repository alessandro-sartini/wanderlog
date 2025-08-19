package com.travel.wanderlog.mapper;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.model.Trip;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true) // lo setti nel service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderInOwner", ignore = true)
    Trip toEntity(TripCreateDto dto);

    @Mapping(target = "ownerId", source = "owner.id")
    TripDto toDto(Trip entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TripUpdateDto dto, @MappingTarget Trip entity);
}
