package com.travel.wanderlog.mapper;

import com.travel.wanderlog.dto.trip.dto.TripCreateDto;
import com.travel.wanderlog.dto.trip.dto.TripDto;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;

import org.mapstruct.*;

// MapStruct (non base):
// - componentModel="spring": il mapper è un bean Spring @Component
// - unmappedTargetPolicy=IGNORE: non esplodere se non mappiamo qualche campo (es. createdAt)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "owner", source = "owner")
    })
    Trip toEntity(TripCreateDto dto, User owner);

    @Mapping(target = "ownerId", source = "owner.id")
    TripDto toDto(Trip entity);
}
// Non serve mappare Visibility/VisibilityDto: i nomi degli enum combaciano,
// MapStruct li converte da solo. Se vuoi essere esplicito, puoi aggiungere
// value mapping.
