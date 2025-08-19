package com.travel.wanderlog.service;

import com.travel.wanderlog.dto.trip.TripCreateDto;
import com.travel.wanderlog.dto.trip.TripDto;
import com.travel.wanderlog.dto.trip.TripShowDto;
import com.travel.wanderlog.dto.trip.TripUpdateDto;
import com.travel.wanderlog.mapper.TripMapper;
import com.travel.wanderlog.mapper.TripViewMapper;
import com.travel.wanderlog.model.Trip;
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

    /**
     * Crea un Trip e setta l’owner (utente seed "demo@travelsage.io").
     */
    @Transactional
    public TripDto create(TripCreateDto dto) {
        var owner = userRepository.findByEmail("demo@travelsage.io")
                .orElseThrow(() -> new IllegalStateException(
                        "Utente demo mancante. Profilo 'dev' attivo e SeedConfig eseguito?"));

        // Validazione base date
        if (dto.startDate() != null && dto.endDate() != null && dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("endDate non può precedere startDate");
        }

        // Se il tuo TripMapper ha toEntity(dto, owner) puoi usarlo direttamente.
        Trip entity = mapper.toEntity(dto, owner);
        entity.setId(null);          // forza INSERT
        // se hai @Version su Trip: entity.setVersion(null);
        entity.setOwner(owner);      // evita owner_id NULL

        Trip saved = tripRepository.save(entity);
        return mapper.toDto(saved);
    }

    /**
     * Patch/Update di un Trip esistente.
     */
    @Transactional
    public TripDto update(Long id, TripUpdateDto dto) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));

        if (dto.startDate() != null && dto.endDate() != null && dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("endDate non può precedere startDate");
        }

        mapper.updateFromDto(dto, t);      // MapStruct: aggiorna solo i non-null
        Trip saved = tripRepository.save(t);
        return mapper.toDto(saved);
    }

    /**
     * Show del Trip: include i DayPlan ordinati per indexInTrip.
     */
    @Transactional
    public TripShowDto show(Long id) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: id=" + id));

        var days = dayPlanRepository.findByTripIdOrderByIndexInTripAsc(id);
        return viewMapper.toShowDto(t, days);
    }

    /**
     * Get minimale del Trip (solo TripDto).
     */
    @Transactional
    public TripDto getById(Long id) {
        Trip t = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip non trovato: " + id));
        return mapper.toDto(t);
    }
}
