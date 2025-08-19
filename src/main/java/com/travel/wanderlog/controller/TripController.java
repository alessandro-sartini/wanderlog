package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.service.TripService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    // per dev: owner = demo@travelsage.io
    @GetMapping
    public List<TripDto> listForDemo() {
        return tripService.listByOwnerEmail("demo@travelsage.io");
    }

    @GetMapping("/all")
    public List<TripDto> All() {
        return tripService.findAll();
    }

    @GetMapping("/owner/{ownerId}")
    public List<TripDto> listByOwner(@PathVariable Long ownerId) {
        return tripService.listByOwner(ownerId);
    }

    @PostMapping
    public ResponseEntity<TripDto> create(@RequestBody TripCreateDto dto) {
        TripDto created = tripService.create(dto);
        return ResponseEntity.created(URI.create("/api/trips/" + created.id())).body(created);
    }

    @PatchMapping("/{id}")
    public TripDto update(@PathVariable Long id, @RequestBody TripUpdateDto dto) {
        return tripService.update(id, dto);
    }

    // REORDER: POST /api/trips/{id}/reorder?to=3
    @PostMapping("/{id}/reorder")
    public ResponseEntity<Void> reorder(@PathVariable Long id, @RequestParam("to") int to) {
        tripService.reorder(id, to);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tripId}")
    public TripShowDto show(@PathVariable Long tripId) {
        return tripService.show(tripId);
    }

    @DeleteMapping("/{tripId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long tripId) {
        tripService.delete(tripId);
    }

}
