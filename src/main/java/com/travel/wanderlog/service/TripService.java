package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.mapper.TripMapper;
import com.travel.wanderlog.mapper.TripViewMapper;
import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;
import com.travel.wanderlog.repository.ActivityRepository;
import com.travel.wanderlog.repository.DayPlanRepository;
import com.travel.wanderlog.repository.TripRepository;
import com.travel.wanderlog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DayPlanRepository dayPlanRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final TripMapper mapper;
    private final TripViewMapper viewMapper;

    public List<TripDto> findAll() {
        return tripRepository.findAll()
                .stream().map(mapper::toDto).toList();
    }

    // lista per ownerId
    @Transactional
    public List<TripDto> listByOwner(Long ownerId) {
        return tripRepository.findByOwnerIdOrderByOrderInOwnerAsc(ownerId)
                .stream().map(mapper::toDto).toList();
    }

    // lista per email (dev/demo)
    @Transactional
    public List<TripDto> listByOwnerEmail(String email) {
        return tripRepository.findByOwnerEmailOrderByOrderInOwnerAsc(email)
                .stream().map(mapper::toDto).toList();
    }

    // create: owner = demo@travelsage.io (finché non c'è auth)
    @Transactional
    public TripDto create(TripCreateDto dto) {
        User owner = userRepository.findByEmail("demo@travelsage.io")
                .orElseThrow(() -> new IllegalStateException("Utente demo non trovato (profilo dev?)"));

        Trip entity = mapper.toEntity(dto);
        entity.setId(null);
        entity.setOwner(owner);

        int max = tripRepository.maxOrderInOwner(owner.getId());
        entity.setOrderInOwner(max + 1); // append in coda

        Trip saved = tripRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public TripDto update(Long id, TripUpdateDto dto) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));
        mapper.updateFromDto(dto, t);
        return mapper.toDto(tripRepository.save(t));
    }

    // REORDER: ?to=3 — tecnica parking per non violare la unique (owner_id,
    // order_in_owner)
    @Transactional
    public void reorder(Long tripId, int to) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + tripId));

        Long ownerId = trip.getOwner().getId();
        int from = trip.getOrderInOwner();
        int max = tripRepository.maxOrderInOwner(ownerId);

        // clamp
        to = Math.max(1, Math.min(to, Math.max(1, max)));
        if (to == from)
            return;

        // 1) parcheggio questo trip (order=0)
        tripRepository.park(tripId);

        // 2) sposto il blocco
        if (to < from) {
            // muovo SU: [to .. from-1] +1
            tripRepository.shiftUpRange(ownerId, to, from - 1);
        } else {
            // muovo GIÙ: [from+1 .. to] -1
            tripRepository.shiftDownRange(ownerId, from + 1, to);
        }

        // 3) posiziono il trip a 'to'
        trip.setOrderInOwner(to);
        tripRepository.save(trip);
    }

    @Transactional
    public TripShowDto show(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + tripId));

        List<DayPlan> days = tripRepository.findDaysByTripIdOrderByIndex(tripId);
        return viewMapper.toShowDto(trip, days);
    }

    @Transactional
    public void delete(Long tripId) {
        // 1) prendo il trip e i dati necessari
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + tripId));

        Long ownerId = trip.getOwner().getId();
        int removedOrder = trip.getOrderInOwner();

        // 1) Activities del trip
        activityRepository.deleteAllByTripId(tripId);

        // 2) DayPlan del trip
        dayPlanRepository.deleteAllByTripId(tripId);

        // 3) Trip
        tripRepository.deleteById(tripId);

        // 4) Ricompatta ordini degli altri trip dello stesso owner
        tripRepository.shiftDownAfter(ownerId, removedOrder);
    }

}
