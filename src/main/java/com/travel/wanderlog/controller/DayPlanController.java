package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.dayPlan.DayPlanCreateDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanShowDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanUpdateDto;
import com.travel.wanderlog.service.DayPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/days")
@RequiredArgsConstructor
public class DayPlanController {

    private final DayPlanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DayPlanDto addDay(@PathVariable Long tripId, @Valid @RequestBody DayPlanCreateDto dto) {
        return service.addDay(tripId, dto);
    }

    @GetMapping
    public List<DayPlanDto> list(@PathVariable Long tripId) {
        return service.listDays(tripId);
    }

    @PatchMapping("/{dayPlanId}")
    public DayPlanDto update(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @RequestBody DayPlanUpdateDto dto) {
        return service.update(tripId, dayPlanId, dto);
    }

    @DeleteMapping("/{dayPlanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long tripId, @PathVariable Long dayPlanId) {
        service.delete(tripId, dayPlanId);
    }

    @GetMapping("/{dayPlanId}/show")
    public DayPlanShowDto show(@PathVariable Long tripId,
            @PathVariable Long dayPlanId) {
        return service.show(tripId, dayPlanId);
    }

    @PostMapping("/{dayPlanId}/reorder")
    public ResponseEntity<Void> reorder(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @RequestParam("to") int to) {
        service.reorder(tripId, dayPlanId, to);
        return ResponseEntity.noContent().build();
    }

}
