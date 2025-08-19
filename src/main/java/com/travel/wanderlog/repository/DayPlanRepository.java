package com.travel.wanderlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.DayPlan;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

    List<DayPlan> findByTripIdOrderByIndexInTripAsc(Long tripId);

    boolean existsByTripIdAndIndexInTrip(Long tripId, Integer indexInTrip);

    // max(indexInTrip) per un trip
    @Query("select coalesce(max(d.indexInTrip), 0) from DayPlan d where d.trip.id = :tripId")
    int maxIndexInTrip(@Param("tripId") Long tripId);

    // --- PARKING: porto il giorno a index 0, fuori dallo schema 1..N ---
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update DayPlan d
              set d.indexInTrip = 0
            where d.id = :dayPlanId
           """)
    int park(@Param("dayPlanId") Long dayPlanId);

    // Shift SU (+1) l’intervallo [fromIdx..toIdx]
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update DayPlan d
              set d.indexInTrip = d.indexInTrip + 1
            where d.trip.id = :tripId
              and d.indexInTrip >= :fromIdx
              and d.indexInTrip <= :toIdx
           """)
    int shiftUpRange(@Param("tripId") Long tripId,
                     @Param("fromIdx") int fromIdx,
                     @Param("toIdx") int toIdx);

    // Shift GIÙ (-1) l’intervallo [fromIdx..toIdx]
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update DayPlan d
              set d.indexInTrip = d.indexInTrip - 1
            where d.trip.id = :tripId
              and d.indexInTrip >= :fromIdx
              and d.indexInTrip <= :toIdx
           """)
    int shiftDownRange(@Param("tripId") Long tripId,
                       @Param("fromIdx") int fromIdx,
                       @Param("toIdx") int toIdx);

    // Dopo DELETE: tutti quelli > removedIndex scendono di 1
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update DayPlan d
              set d.indexInTrip = d.indexInTrip - 1
            where d.trip.id = :tripId
              and d.indexInTrip > :removedIndex
           """)
    int shiftDownAfter(@Param("tripId") Long tripId,
                       @Param("removedIndex") int removedIndex);
}

