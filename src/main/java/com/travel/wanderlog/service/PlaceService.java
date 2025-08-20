package com.travel.wanderlog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.provider.PlaceSearchProvider;

@Service
@lombok.RequiredArgsConstructor
public class PlaceService {
    private final PlaceSearchProvider provider;

    public List<PlaceDto> search(String q, Double lat, Double lon, Integer limit) {
        return provider.search(q, lat, lon, limit);
    }

    public Optional<PlaceDto> details(String providerPlaceId) {
        return provider.details(providerPlaceId);
    }

    public Optional<PlaceDto> reverse(double lat, double lon) {
        return provider.reverse(lat, lon);
    }
}