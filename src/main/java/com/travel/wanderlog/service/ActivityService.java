package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.activity.ActivityCreateDto;
import com.travel.wanderlog.dto.activity.ActivityDto;
import com.travel.wanderlog.dto.activity.ActivityUpdateDto;
import com.travel.wanderlog.mapper.ActivityMapper;
import com.travel.wanderlog.model.Activity;
import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.repository.ActivityRepository;
import com.travel.wanderlog.repository.DayPlanRepository;
// import com.travel.wanderlog.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    // private final TripRepository tripRepository;
    private final DayPlanRepository dayPlanRepository;
    private final ActivityRepository activityRepository;
    private final ActivityMapper mapper;

    private DayPlan mustLoadDayPlan(Long tripId, Long dayPlanId) {
        DayPlan dp = dayPlanRepository.findById(dayPlanId)
                .orElseThrow(() -> new IllegalArgumentException("DayPlan non trovato: id=" + dayPlanId));
        Trip trip = dp.getTrip();
        if (!trip.getId().equals(tripId)) {
            throw new IllegalArgumentException("DayPlan non appartiene al trip indicato");
        }
        return dp;
    }

    // @Transactional
    // public ActivityDto add(Long tripId, Long dayPlanId, ActivityCreateDto dto) {
    // DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

    // if (activityRepository.existsByDayPlanIdAndOrderInDay(dp.getId(),
    // dto.orderInDay())) {
    // throw new IllegalArgumentException("orderInDay già usato per questo
    // dayPlan");
    // }

    // Activity entity = mapper.toEntity(dto, dp);
    // Activity saved = activityRepository.save(entity);
    // return mapper.toDto(saved);
    // }

    @Transactional
public ActivityDto add(Long tripId, Long dayPlanId, ActivityCreateDto dto) {
    DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

    int max = activityRepository.findMaxOrderInDay(dp.getId()); // 0 se non ce ne sono
    int newOrder;

    if (dto.orderInDay() == null) {
        // append in coda
        newOrder = max + 1;
    } else {
        // clamp a [1, max+1]
        newOrder = Math.max(1, Math.min(dto.orderInDay(), max + 1));
        if (newOrder <= max) {
            // inserisco in mezzo: sposto su chi sta da newOrder .. max
            activityRepository.shiftUpRange(dp.getId(), newOrder, max);
        }
    }

    Activity entity = mapper.toEntity(dto, dp);
    entity.setOrderInDay(newOrder);

    Activity saved = activityRepository.save(entity);
    return mapper.toDto(saved);
}

    @Transactional
    public List<ActivityDto> list(Long tripId, Long dayPlanId) {
        DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);
        return activityRepository.findByDayPlanIdOrderByOrderInDayAsc(dp.getId())
                .stream().map(mapper::toDto).toList();
    }

    // @Transactional
    // public ActivityDto update(Long tripId, Long dayPlanId, Long activityId,
    // ActivityUpdateDto dto) {

    // DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);
    // Activity a = activityRepository.findById(activityId)
    // .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: id=" +
    // activityId));
    // if (!a.getDayPlan().getId().equals(dp.getId())) {
    // throw new IllegalArgumentException("Activity non appartiene al dayPlan
    // indicato");
    // }

    // // se cambia orderInDay, verifica unicità
    // if (dto.orderInDay() != null && !dto.orderInDay().equals(a.getOrderInDay()))
    // {
    // if (activityRepository.existsByDayPlanIdAndOrderInDay(dp.getId(),
    // dto.orderInDay())) {
    // throw new IllegalArgumentException("orderInDay già usato per questo
    // dayPlan");
    // }
    // a.setOrderInDay(dto.orderInDay());
    // }

    // mapper.updateFromDto(dto, a);
    // Activity saved = activityRepository.save(a);
    // return mapper.toDto(saved);
    // }

    @Transactional
    public ActivityDto update(Long tripId, Long dayPlanId, Long activityId, ActivityUpdateDto dto) {
        DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

        Activity a = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: id=" + activityId));
        if (!a.getDayPlan().getId().equals(dp.getId())) {
            throw new IllegalArgumentException("Activity non appartiene al dayPlan indicato");
        }

        if (dto.orderInDay() != null && !dto.orderInDay().equals(a.getOrderInDay())) {
            int oldOrder = a.getOrderInDay();
            int maxOrder = activityRepository.findByDayPlanIdOrderByOrderInDayAsc(dp.getId()).size();
            int newOrder = Math.max(1, Math.min(dto.orderInDay(), maxOrder));

            if (newOrder < oldOrder) {
                // muovo su: [newOrder .. oldOrder-1] ++
                activityRepository.shiftUpRange(dp.getId(), newOrder, oldOrder - 1);
            } else {
                // muovo giù: [oldOrder+1 .. newOrder] --
                activityRepository.shiftDownRange(dp.getId(), oldOrder + 1, newOrder);
            }
            a.setOrderInDay(newOrder);
        }

        // aggiorna gli altri campi (NullValuePropertyMappingStrategy.IGNORE in
        // MapStruct)
        mapper.updateFromDto(dto, a);

        Activity saved = activityRepository.save(a);
        return mapper.toDto(saved);
    }

    // @Transactional
    // public void delete(Long tripId, Long dayPlanId, Long activityId) {
    // DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);
    // Activity a = activityRepository.findById(activityId)
    // .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: id=" +
    // activityId));
    // if (!a.getDayPlan().getId().equals(dp.getId())) {
    // throw new IllegalArgumentException("Activity non appartiene al dayPlan
    // indicato");
    // }
    // activityRepository.delete(a);
    // }

    @Transactional
    public void delete(Long tripId, Long dayPlanId, Long activityId) {
        DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

        Activity entity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity non trovata: " + activityId));
        if (!entity.getDayPlan().getId().equals(dp.getId())) {
            throw new IllegalArgumentException("Activity non appartiene al dayPlan indicato");
        }

        int removedOrder = entity.getOrderInDay();
        activityRepository.delete(entity);
        activityRepository.shiftDownAfter(dp.getId(), removedOrder);
    }

}
