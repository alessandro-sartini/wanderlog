// TripController.java  (aggiunte PATCH + SHOW)
package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto create(@Valid @RequestBody TripCreateDto dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public TripDto get(@PathVariable Long id) {
        return service.getById(id);
    }

    // PATCH parziale
    @PatchMapping("/{id}")
    public TripDto update(@PathVariable Long id, @RequestBody TripUpdateDto dto) {
        return service.update(id, dto);
    }

    // SHOW: + giorni
    @GetMapping("/{id}/show")
    public TripShowDto show(@PathVariable Long id) {
        return service.show(id);
    }
}
