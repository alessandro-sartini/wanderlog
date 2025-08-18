package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.trip.dto.TripCreateDto;
import com.travel.wanderlog.dto.trip.dto.TripDto;
import com.travel.wanderlog.service.TripService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/trips", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TripController {

    private final TripService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TripDto create(@Valid @RequestBody TripCreateDto dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public TripDto get(@PathVariable Long id) {
        return service.getById(id);
    }

    // src/main/java/com/travel/wanderlog/controller/TripController.java
    @GetMapping("/me")
    public List<TripDto> listMine() {
        return service.listMine();
    }

}
