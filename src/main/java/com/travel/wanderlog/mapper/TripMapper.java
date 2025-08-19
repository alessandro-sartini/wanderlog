package com.travel.wanderlog.mapper;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;

import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripMapper {

    @Mapping(target = "id", ignore = true)
    // @Mapping(target = "version", ignore = true) // se usi @Version
    @Mapping(target = "owner", ignore = true) // la setti nel service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Trip toEntity(TripCreateDto dto, User owner);

    @Mapping(target = "ownerId", source = "owner.id")
    TripDto toDto(Trip entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(TripUpdateDto dto, @MappingTarget Trip entity);
}
