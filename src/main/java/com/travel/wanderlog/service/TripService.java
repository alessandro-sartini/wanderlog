package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.trip.dto.TripCreateDto;
import com.travel.wanderlog.dto.trip.dto.TripDto;
import com.travel.wanderlog.mapper.TripMapper;
import com.travel.wanderlog.model.Trip;
import com.travel.wanderlog.model.User;
import com.travel.wanderlog.repository.TripRepository;
import com.travel.wanderlog.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper mapper;
    private final UserRepository userRepository;

    @Transactional
    public TripDto create(TripCreateDto dto) {
        User owner = userRepository.findByEmail("demo@travelsage.io")
                .orElseThrow(() -> new IllegalStateException("Owner demo non trovato (seed dev mancante)"));

        if (dto.startDate() != null && dto.endDate() != null &&
                dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("endDate non può precedere startDate");
        }

        Trip entity = mapper.toEntity(dto, owner); // <-- mapping MapStruct (dto + owner)
        Trip saved = tripRepository.save(entity);
        return mapper.toDto(saved); // <-- mapping MapStruct (entity -> dto)
    }

    @Transactional
    public TripDto getById(Long id) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + id));
        return mapper.toDto(t);
    }

    // src/main/java/com/travel/wanderlog/service/TripService.java
    @Transactional
    public List<TripDto> listMine() {
        String email = "demo@travelsage.io";
        var trips = tripRepository.findAllByOwnerEmailOrderByIdDesc(email);
        return trips.stream().map(mapper::toDto).toList();
    }

}
