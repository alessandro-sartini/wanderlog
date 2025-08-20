package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.activity.ActivityCreateDto;
import com.travel.wanderlog.dto.activity.ActivityDto;
import com.travel.wanderlog.dto.activity.ActivityUpdateDto;
import com.travel.wanderlog.dto.order.ActivityReorderDto;
import com.travel.wanderlog.dto.place.PlaceAttachDto;
import com.travel.wanderlog.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/days/{dayPlanId}/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityDto add(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @Valid @RequestBody ActivityCreateDto dto) {
        return service.add(tripId, dayPlanId, dto);
    }

    @GetMapping
    public List<ActivityDto> list(@PathVariable Long tripId,
            @PathVariable Long dayPlanId) {
        return service.list(tripId, dayPlanId);
    }

    @PatchMapping("/{activityId}")
    public ActivityDto update(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @PathVariable Long activityId,
            @RequestBody ActivityUpdateDto dto) {
        return service.update(tripId, dayPlanId, activityId, dto);
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @PathVariable Long activityId) {
        service.delete(tripId, dayPlanId, activityId);
    }

    @GetMapping("/{activityId}")
    public ActivityDto show(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @PathVariable Long activityId) {
        return service.show(tripId, dayPlanId, activityId);
    }

    /**
     * PATCH per riordinare una activity alla posizione target.
     * Esempio URL:
     * PATCH /api/trips/1/days/10/activities/55/reorder
     * Body:
     * { "targetOrder": 2 }
     */
    @PatchMapping("/{activityId}/reorder")
    public ActivityDto reorder(@PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityReorderDto dto) {
        return service.reorder(tripId, dayPlanId, activityId, dto);
    }

    @PostMapping("/from-place")
    public ResponseEntity<ActivityDto> createFromPlace(
            @PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @RequestBody @Valid PlaceAttachDto body) {
        ActivityDto created = service.createFromPlace(tripId, dayPlanId, body);
        URI location = URI.create(String.format("/api/trips/%d/days/%d/activities/%d",
                tripId, dayPlanId, created.id()));
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{activityId}/place")
    public ActivityDto attachPlace(
            @PathVariable Long tripId,
            @PathVariable Long dayPlanId,
            @PathVariable Long activityId,
            @RequestBody @Valid PlaceAttachDto body) {
        return service.attachPlace(tripId, dayPlanId, activityId, body);
    }

}