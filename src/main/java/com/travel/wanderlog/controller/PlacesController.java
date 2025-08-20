package com.travel.wanderlog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.service.PlaceService;

@RestController
@lombok.RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlacesController {
    private final PlaceService places;

    @GetMapping("/search")
    public List<PlaceDto> search(
            @RequestParam String term,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return places.search(term, lat, lon, limit);
    }

    @GetMapping("/details/{providerPlaceId}")
    public PlaceDto details(@PathVariable String providerPlaceId) {
        return places.details(providerPlaceId).orElseThrow(() -> new IllegalArgumentException("Place non trovato"));
    }

    @GetMapping("/reverse")
    public PlaceDto reverse(@RequestParam double lat, @RequestParam double lon) {
        return places.reverse(lat, lon).orElseThrow(() -> new IllegalArgumentException("Nessun risultato"));
    }
}
