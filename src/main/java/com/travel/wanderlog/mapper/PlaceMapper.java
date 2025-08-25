package com.travel.wanderlog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.model.Place;
import java.util.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlaceMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "categoriesCsv", source = "categories", qualifiedByName = "joinCsv"),
            // @Mapping(target = "photos", source = "photos", qualifiedByName = "joinCsv")
    })
    Place toEntity(PlaceDto dto);

    @Mapping(target = "categories", source = "categoriesCsv", qualifiedByName = "splitCsv")
    // @Mapping(target = "photos", source = "photos", qualifiedByName = "splitCsv")
    PlaceDto toDto(Place entity);

    @Named("joinCsv")
    static String joinCsv(List<String> list) {
        if (list == null || list.isEmpty())
            return null;
        String joined = list.stream().filter(Objects::nonNull).map(String::trim)
                .filter(s -> !s.isBlank()).reduce((a, b) -> a + "," + b).orElse("");
        return joined.isBlank() ? null : joined;
    }

    @Named("splitCsv")
    static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank())
            return Collections.emptyList();
        return Arrays.stream(csv.split(",")).map(String::trim)
                .filter(s -> !s.isBlank()).toList();
    }
}
