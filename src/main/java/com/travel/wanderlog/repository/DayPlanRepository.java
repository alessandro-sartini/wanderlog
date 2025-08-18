package com.travel.wanderlog.repository;

import com.travel.wanderlog.model.DayPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
    List<DayPlan> findByTripIdOrderByIndexInTripAsc(Long tripId);

    boolean existsByTripIdAndIndexInTrip(Long tripId, Integer indexInTrip);
}
