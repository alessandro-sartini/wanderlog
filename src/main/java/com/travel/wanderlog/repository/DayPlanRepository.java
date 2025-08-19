package com.travel.wanderlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.DayPlan;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

       boolean existsByTripIdAndIndexInTrip(Long tripId, Integer indexInTrip);

       List<DayPlan> findByTripIdOrderByIndexInTripAsc(Long tripId);

       @Query("select coalesce(max(dp.indexInTrip), 0) from DayPlan dp where dp.trip.id = :tripId")
       int maxIndexInTrip(@Param("tripId") Long tripId);

       // parcheggia il dayplan a index = 0 (evita la unique (trip_id, index_in_trip))
       @Modifying(clearAutomatically = true, flushAutomatically = true)
       @Query("update DayPlan dp set dp.indexInTrip = 0 where dp.id = :dayPlanId")
       int park(@Param("dayPlanId") Long dayPlanId);

       // muovi SU: [from..to] +1
       @Modifying(clearAutomatically = true, flushAutomatically = true)
       @Query("""
                     update DayPlan dp
                        set dp.indexInTrip = dp.indexInTrip + 1
                      where dp.trip.id = :tripId
                        and dp.indexInTrip >= :fromIndex
                        and dp.indexInTrip <= :toIndex
                     """)
       int shiftUpRange(@Param("tripId") Long tripId,
                     @Param("fromIndex") int fromIndex,
                     @Param("toIndex") int toIndex);

       // muovi GIÙ: [from..to] -1
       @Modifying(clearAutomatically = true, flushAutomatically = true)
       @Query("""
                     update DayPlan dp
                        set dp.indexInTrip = dp.indexInTrip - 1
                      where dp.trip.id = :tripId
                        and dp.indexInTrip >= :fromIndex
                        and dp.indexInTrip <= :toIndex
                     """)
       int shiftDownRange(@Param("tripId") Long tripId,
                     @Param("fromIndex") int fromIndex,
                     @Param("toIndex") int toIndex);

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
