package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.mapper.TripMapper;
import com.travel.wanderlog.mapper.TripViewMapper;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;
import com.travel.wanderlog.repository.DayPlanRepository;
import com.travel.wanderlog.repository.TripRepository;
import com.travel.wanderlog.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DayPlanRepository dayPlanRepository;
    private final TripMapper mapper;
    private final TripViewMapper viewMapper;
    private final UserRepository userRepository;

    @Transactional
    public TripDto create(TripCreateDto dto) {
        User owner = userRepository.findByEmail("demo@travelsage.io")
                .orElseThrow(() -> new IllegalStateException("Owner demo non trovato"));
        if (dto.startDate() != null && dto.endDate() != null && dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("endDate non può precedere startDate");
        }
        Trip entity = mapper.toEntity(dto, owner);
        return mapper.toDto(tripRepository.save(entity));
    }

    @Transactional
    public TripDto update(Long id, TripUpdateDto dto) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));

        // valida solo se entrambi presenti
        if (dto.startDate() != null && dto.endDate() != null && dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("endDate non può precedere startDate");
        }

        mapper.updateFromDto(dto, t);
        return mapper.toDto(tripRepository.save(t));
    }

    // @Transactional
    // public TripShowDto show(Long id) {
    // Trip t = tripRepository.findById(id)
    // .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));

    // List<DayPlan> days = tripRepository.findDaysByTripIdOrderByIndex(id);

    // List<DayPlanSummaryDto> daySummaries = days.stream()
    // .map(dp -> new DayPlanSummaryDto(
    // dp.getId(),
    // dp.getIndexInTrip(),
    // dp.getDate(),
    // dp.getTitle(),
    // Math.toIntExact(activityRepository.countByDayPlanId(dp.getId()))
    // )).toList();

    // return new TripShowDto(
    // t.getId(),
    // t.getOwner().getId(),
    // t.getTitle(),
    // t.getDescription(),
    // t.getStartDate(),
    // t.getEndDate(),
    // VisibilityDto.valueOf(t.getVisibility().name()),
    // daySummaries
    // );
    // }
    @Transactional
    public TripShowDto show(Long id) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + id));

        var days = dayPlanRepository.findByTripIdOrderByIndexInTripAsc(id);
        return viewMapper.toShowDto(t, days);
    }

    @Transactional
    public TripDto getById(Long id) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));
        return mapper.toDto(t);
    }
}