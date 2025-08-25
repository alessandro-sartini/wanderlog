package com.travel.wanderlog.service;

import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.model.Place;
import com.travel.wanderlog.provider.PlaceSearchProvider;
import com.travel.wanderlog.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceSearchProvider provider; // oggi Nominatim, domani Google
    private final PlaceRepository placeRepo; // catalogo locale

    /** Ricerca ibrida: prima DB locale, poi provider esterno per completare. */
    public List<PlaceDto> search(String q, Double lat, Double lon, Integer limit) {
        final int lim = (limit == null || limit <= 0) ? 10 : Math.min(limit, 20);

        // 1) risultati locali
        List<Place> local = placeRepo.searchLocal(q, PageRequest.of(0, lim));
        List<PlaceDto> out = new ArrayList<>(local.size());
        Set<String> seen = new HashSet<>();

        for (Place p : local) {
            PlaceDto dto = toCatalogDto(p);
            if (seen.add(key(dto)))
                out.add(dto);
        }

        // 2) completa con provider esterno se serve
        int remaining = lim - out.size();
        if (remaining > 0) {
            List<PlaceDto> ext = provider.search(q, lat, lon, remaining);
            for (PlaceDto e : ext) {
                // se esiste già in catalogo, preferisci il “catalog”
                placeRepo.findByProviderAndProviderPlaceId(e.provider(), e.providerPlaceId())
                        .map(this::toCatalogDto)
                        .ifPresentOrElse(dto -> {
                            if (seen.add(key(dto)))
                                out.add(dto);
                        },
                                () -> {
                                    if (seen.add(key(e)))
                                        out.add(e);
                                });
            }
        }

        return out;
    }

    /** Dettagli/reverse: per ora lascio pass-through al provider. */
    public Optional<PlaceDto> details(String providerPlaceId) {
        return provider.details(providerPlaceId);
    }

    public Optional<PlaceDto> reverse(double lat, double lon) {
        return provider.reverse(lat, lon);
    }

    // ---- helpers ----
    private static String key(PlaceDto p) {
        return p.provider() + ":" + p.providerPlaceId();
    }

    /** Mappa una entity Place del nostro DB in un PlaceDto “catalog”. */
    private PlaceDto toCatalogDto(Place p) {
        return new PlaceDto(
                "catalog",
                String.valueOf(p.getId()),
                p.getName(),
                p.getFormattedAddress(),
                p.getLat(),
                p.getLon(),
                null,
                p.getRating(), // se non hai il campo, passa null
                p.getUserRatingsTotal(), // idem
                p.getPriceLevel(), // idem
               null // idem
        );
    }
}
