package com.travel.wanderlog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.mapper.PlaceMapper;
import com.travel.wanderlog.model.Place;
import com.travel.wanderlog.repository.PlaceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// PlaceCatalogService.java
@Service
@RequiredArgsConstructor
public class PlaceCatalogService {
    private final PlaceRepository repo;
    private final PlaceMapper mapper;           // se lo usi
    private final PlaceService placeProvider;   // il provider (Nominatim/Google) per i details

    @Transactional
    public PlaceDto upsertFromProvider(String provider, String providerPlaceId) {
        // 1) già in cache/DB?
        Optional<Place> existing = repo.findByProviderAndProviderPlaceId(provider, providerPlaceId);
        if (existing.isPresent()) {
            return mapper.toDto(existing.get());
        }

        // 2) arricchisci con details dal provider
        PlaceDto full = placeProvider.details(providerPlaceId)
            .orElseThrow(() -> new IllegalArgumentException("Place non trovato sul provider: " + providerPlaceId));

        // 3) salva
        Place entity = mapper.toEntity(full);
        // IMPORTANTE: imposta provider dal parametro (alcuni provider non lo rimandano in details)
        entity.setProvider(provider);
        entity.setProviderPlaceId(providerPlaceId);

        Place saved = repo.save(entity);
        return mapper.toDto(saved);
    }


      public List<PlaceDto> searchLocal(String term, Double lat, Double lon, int limit) {
        return repo.searchLocal(term).stream()
                .limit(limit)
                .map(mapper::toDto)
                .toList();
    }
}
