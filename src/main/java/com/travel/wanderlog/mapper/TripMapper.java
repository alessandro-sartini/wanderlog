package com.travel.wanderlog.mapper;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;

import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripMapper {

    @Mapping(target = "owner", source = "owner")
    Trip toEntity(TripCreateDto dto, User owner);

    @Mapping(target = "ownerId", source = "owner.id")
    TripDto toDto(Trip entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TripUpdateDto dto, @MappingTarget Trip entity);
}
