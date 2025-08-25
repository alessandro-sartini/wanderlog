package com.travel.wanderlog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.travel.wanderlog.dto.place.PlaceDto;
import com.travel.wanderlog.service.PlaceCatalogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
class PlaceCatalogController {
  private final PlaceCatalogService catalog;

  @GetMapping("/catalog")
  public List<PlaceDto> searchLocal(
      @RequestParam(required = false) String text,
      @RequestParam(required = false) Double lat,
      @RequestParam(required = false) Double lon,
      @RequestParam(defaultValue = "10") int limit
  ) {
    return catalog.searchLocal(text, lat, lon, limit);
  }
}
