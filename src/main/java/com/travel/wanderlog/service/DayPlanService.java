package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.activity.ActivityDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanCreateDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanShowDto;
import com.travel.wanderlog.dto.dayPlan.DayPlanUpdateDto;
import com.travel.wanderlog.mapper.DayPlanMapper;
import com.travel.wanderlog.mapper.DayPlanViewMapper;
import com.travel.wanderlog.model.Activity;
import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.repository.ActivityRepository;
import com.travel.wanderlog.repository.DayPlanRepository;
import com.travel.wanderlog.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DayPlanService {

    private final TripRepository tripRepository;
    private final DayPlanRepository dayPlanRepository;
    private final DayPlanMapper mapper;
    private final ActivityRepository activityRepository;
    private final DayPlanViewMapper viewMapper;

    @Transactional
    public DayPlanDto addDay(Long tripId, DayPlanCreateDto dto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + tripId));

        if (dayPlanRepository.existsByTripIdAndIndexInTrip(tripId, dto.indexInTrip())) {
            throw new IllegalArgumentException("indexInTrip già usato per questo trip");
        }
        if (trip.getStartDate() != null && trip.getEndDate() != null && dto.date() != null) {
            if (dto.date().isBefore(trip.getStartDate()) || dto.date().isAfter(trip.getEndDate())) {
                throw new IllegalArgumentException("date fuori dal range del trip");
            }
        }

        DayPlan entity = mapper.toEntity(dto, trip);
        DayPlan saved = dayPlanRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public List<DayPlanDto> listDays(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new IllegalArgumentException("Trip non trovato: id=" + tripId);
        }
        return dayPlanRepository.findByTripIdOrderByIndexInTripAsc(tripId)
                .stream().map(mapper::toDto).toList();
    }

    @Transactional
    public DayPlanDto update(Long tripId, Long dayPlanId, DayPlanUpdateDto dto) {
        DayPlan dp = dayPlanRepository.findById(dayPlanId)
                .orElseThrow(() -> new IllegalArgumentException("DayPlan non trovato: id=" + dayPlanId));
        if (!dp.getTrip().getId().equals(tripId)) {
            throw new IllegalArgumentException("DayPlan non appartiene al trip indicato");
        }

        // se cambia l'indice, verifica unicità
        if (dto.indexInTrip() != null && !dto.indexInTrip().equals(dp.getIndexInTrip())) {
            if (dayPlanRepository.existsByTripIdAndIndexInTrip(tripId, dto.indexInTrip())) {
                throw new IllegalArgumentException("indexInTrip già usato per questo trip");
            }
            dp.setIndexInTrip(dto.indexInTrip());
        }

        // validazione date nel range del trip
        if (dto.date() != null) {
            Trip trip = dp.getTrip();
            if (trip.getStartDate() != null && trip.getEndDate() != null) {
                if (dto.date().isBefore(trip.getStartDate()) || dto.date().isAfter(trip.getEndDate())) {
                    throw new IllegalArgumentException("date fuori dal range del trip");
                }
            }
        }

        mapper.updateFromDto(dto, dp); // PATCH: aggiorna solo i campi != null
        DayPlan saved = dayPlanRepository.save(dp);
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long tripId, Long dayPlanId) {
        DayPlan dp = dayPlanRepository.findById(dayPlanId)
                .orElseThrow(() -> new IllegalArgumentException("DayPlan non trovato: id=" + dayPlanId));
        if (!dp.getTrip().getId().equals(tripId)) {
            throw new IllegalArgumentException("DayPlan non appartiene al trip indicato");
        }
        dayPlanRepository.delete(dp);
    }

    private DayPlan mustLoadDayPlan(Long tripId, Long dayPlanId) {
        DayPlan dp = dayPlanRepository.findById(dayPlanId)
                .orElseThrow(() -> new IllegalArgumentException("DayPlan non trovato: " + dayPlanId));
        Trip trip = dp.getTrip();
        if (!trip.getId().equals(tripId)) {
            throw new IllegalArgumentException("DayPlan non appartiene al trip indicato");
        }
        return dp;
    }

    // @Transactional
    // public DayPlanDto update(Long tripId, Long dayPlanId, DayPlanUpdateDto dto) {
    // DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);
    // mapper.updateFromDto(dto, dp);
    // return mapper.toDto(dayPlanRepository.save(dp));
    // }

    // @Transactional
    // public DayPlanShowDto show(Long tripId, Long dayPlanId) {
    // DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

    // List<ActivityDto> activities = activityRepository
    // .findByDayPlanIdOrderByOrderInDayAsc(dp.getId())
    // .stream().map(mapper::toDto).toList();

    // return new DayPlanShowDto(
    // dp.getId(),
    // dp.getTrip().getId(),
    // dp.getDate(),
    // dp.getIndexInTrip(),
    // dp.getTitle(),
    // dp.getNote(),
    // dp.getMainPlaceName(),
    // dp.getMainPlaceAddress(),
    // dp.getMainPlacePlaceId(),
    // dp.getMainPlaceLatitude(),
    // dp.getMainPlaceLongitude(),
    // activities);
    // }

    @Transactional
    public DayPlanShowDto show(Long tripId, Long dayPlanId) {
        DayPlan dp = mustLoadDayPlan(tripId, dayPlanId);

        List<Activity> activities = activityRepository
                .findByDayPlanIdOrderByOrderInDayAsc(dp.getId());

        return viewMapper.toShowDto(dp, activities);
    }
}
