package com.travel.wanderlog.service;

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

    // @Transactional
    // public DayPlanDto addDay(Long tripId, DayPlanCreateDto dto) {
    // Trip trip = tripRepository.findById(tripId)
    // .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" +
    // tripId));

    // if (dayPlanRepository.existsByTripIdAndIndexInTrip(tripId,
    // dto.indexInTrip())) {
    // throw new IllegalArgumentException("indexInTrip già usato per questo trip");
    // }
    // if (trip.getStartDate() != null && trip.getEndDate() != null && dto.date() !=
    // null) {
    // if (dto.date().isBefore(trip.getStartDate()) ||
    // dto.date().isAfter(trip.getEndDate())) {
    // throw new IllegalArgumentException("date fuori dal range del trip");
    // }
    // }

    // DayPlan entity = mapper.toEntity(dto, trip);
    // DayPlan saved = dayPlanRepository.save(entity);
    // return mapper.toDto(saved);
    // }
    @Transactional
    public DayPlanDto addDay(Long tripId, DayPlanCreateDto dto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + tripId));

        // opzionale: validazioni su date nel range del trip come avevi già
        if (trip.getStartDate() != null && trip.getEndDate() != null && dto.date() != null) {
            if (dto.date().isBefore(trip.getStartDate()) || dto.date().isAfter(trip.getEndDate())) {
                throw new IllegalArgumentException("date fuori dal range del trip");
            }
        }

        int max = dayPlanRepository.maxIndexInTrip(tripId);
        int newIndex;

        if (dto.indexInTrip() == null) {
            // append in coda
            newIndex = max + 1;
        } else {
            // clamp a [1 .. max+1]
            newIndex = Math.max(1, Math.min(dto.indexInTrip(), max + 1));
            if (newIndex <= max) {
                // inserimento in mezzo: fai spazio spostando su [newIndex .. max]
                dayPlanRepository.shiftUpRange(tripId, newIndex, max);
            }
        }

        DayPlan entity = mapper.toEntity(dto, trip);
        entity.setIndexInTrip(newIndex);

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

        // validazione date nel range del trip (come avevi già)
        if (dto.date() != null) {
            Trip trip = dp.getTrip();
            if (trip.getStartDate() != null && trip.getEndDate() != null) {
                if (dto.date().isBefore(trip.getStartDate()) || dto.date().isAfter(trip.getEndDate())) {
                    throw new IllegalArgumentException("date fuori dal range del trip");
                }
            }
        }

        // cambio indice con clamp + PARKING per evitare collisioni sull’UK
        if (dto.indexInTrip() != null && !dto.indexInTrip().equals(dp.getIndexInTrip())) {
            int oldIdx = dp.getIndexInTrip();
            int max = dayPlanRepository.maxIndexInTrip(tripId); // N attuale
            int target = Math.max(1, Math.min(dto.indexInTrip(), max)); // clamp a [1..N]

            if (target != oldIdx) {
                // 1) Parcheggia il giorno (lo togli dal “giro” dell’UK)
                dayPlanRepository.park(dp.getId());
                dp.setIndexInTrip(0); // tieni allineata anche l’entity in memoria

                // 2) Shifta gli altri per chiudere/aprire il "buco"
                if (target < oldIdx) {
                    // es: 5 -> 2 => [2..4] ++
                    dayPlanRepository.shiftUpRange(tripId, target, oldIdx - 1);
                } else {
                    // es: 2 -> 5 => [3..5] --
                    dayPlanRepository.shiftDownRange(tripId, oldIdx + 1, target);
                }

                // 3) Rimetti il giorno nella posizione target
                dp.setIndexInTrip(target);
            }
        }

        // altri campi (title/note/date/poi…) via mapper (PATCH: solo non-null)
        mapper.updateFromDto(dto, dp);

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

        int removedIndex = dp.getIndexInTrip();

        // 1) elimina prima tutte le attività del giorno
        activityRepository.deleteAllByDayPlanId(dp.getId());

        // 2) elimina il day plan
        dayPlanRepository.delete(dp);

        // 3) compatta gli indici dei restanti day plan del trip
        dayPlanRepository.shiftDownAfter(tripId, removedIndex);
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
