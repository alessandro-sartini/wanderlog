package com.travel.wanderlog.provider;

import java.util.List;
import java.util.Optional;

import com.travel.wanderlog.dto.place.PlaceDto;

public interface PlaceSearchProvider {
    List<PlaceDto> search(String query, Double lat, Double lon, Integer limit);

    Optional<PlaceDto> details(String providerPlaceId);

    Optional<PlaceDto> reverse(double lat, double lon);

    String providerName();
}
