package com.travel.wanderlog.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.*;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.service.PlaceCatalogService;
import com.travel.wanderlog.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlacesController {

    private final PlaceCatalogService catalog;
    private final PlaceService provider;

    // DB-first, poi provider per riempire fino a "limit"
    @GetMapping("/search")
    public List<PlaceDto> search(
            @RequestParam(name = "term") String term,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "false") boolean onlyLocal) {

        int lim = Math.max(1, limit);
        List<PlaceDto> local = catalog.searchLocal(term, lat, lon, lim);

        if (onlyLocal || local.size() >= lim) {
            return local; // niente chiamata esterna
        }

        int remaining = lim - local.size();
        List<PlaceDto> external = provider.search(term, lat, lon, remaining);

        // merge evitando duplicati per chiave provider:providerPlaceId
        Set<String> seen = new HashSet<>();
        List<PlaceDto> out = new ArrayList<>(lim);
        for (PlaceDto p : local) {
            if (seen.add(p.provider() + ":" + p.providerPlaceId()))
                out.add(p);
        }
        for (PlaceDto p : external) {
            if (seen.add(p.provider() + ":" + p.providerPlaceId()))
                out.add(p);
            if (out.size() >= lim)
                break;
        }
        return out;
    }

  

    // @GetMapping("/details")
    // public PlaceDto details(@RequestParam String provider,
    // @RequestParam String providerPlaceId) {
    // // prima prova il catalogo locale
    // return catalog.findByProviderPid(provider, providerPlaceId)
    // .or(() -> provider.details(providerPlaceId))
    // .orElseThrow(() -> new IllegalArgumentException("Place non trovato"));
    // }

    @GetMapping("/reverse")
    public PlaceDto reverse(@RequestParam double lat, @RequestParam double lon) {
        return provider.reverse(lat, lon)
                .orElseThrow(() -> new IllegalArgumentException("Nessun place per queste coordinate"));
    }
}
