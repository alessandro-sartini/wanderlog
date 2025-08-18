package com.travel.wanderlog.controller;

import com.travel.wanderlog.dto.activity.ActivityCreateDto;
import com.travel.wanderlog.dto.activity.ActivityDto;
import com.travel.wanderlog.dto.activity.ActivityUpdateDto;
import com.travel.wanderlog.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}