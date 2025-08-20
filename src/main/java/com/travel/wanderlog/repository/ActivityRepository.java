package com.travel.wanderlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.Activity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
  List<Activity> findByDayPlanIdOrderByOrderInDayAsc(Long dayPlanId);

  void deleteAllByDayPlanId(Long dayPlanId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      delete from Activity a
       where a.dayPlan.id in (
         select d.id from DayPlan d
          where d.trip.id = :tripId
       )
      """)
  int deleteAllByTripId(@Param("tripId") Long tripId);

  boolean existsByDayPlanIdAndOrderInDay(Long dayPlanId, Integer orderInDay);

  @Modifying
  @Query("""
        UPDATE Activity a
           SET a.orderInDay = a.orderInDay - 1
         WHERE a.dayPlan.id = :dayPlanId
           AND a.orderInDay > :fromOrder
      """)
  int shiftDownAfter(@Param("dayPlanId") Long dayPlanId,
      @Param("fromOrder") Integer fromOrder);

  @Query("select coalesce(max(a.orderInDay),0) from Activity a where a.dayPlan.id = :dayPlanId")
  int findMaxOrderInDay(@Param("dayPlanId") Long dayPlanId);

  // Parcheggia l’activity: la porto a orderInDay = 0 (fuori dallo schema 1..N)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Activity a
         set a.orderInDay = 0
       where a.id = :activityId
      """)
  int park(@Param("activityId") Long activityId);

  // Shift “verso l’alto” (usato quando sposti SU un’attività: 5 -> 2)
  // Tutte le attività nel range [from..to] diventano +1 (2->3, 3->4, 4->5)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Activity a
         set a.orderInDay = a.orderInDay + 1
       where a.dayPlan.id = :dayPlanId
         and a.orderInDay >= :fromOrder
         and a.orderInDay <= :toOrder
      """)
  int shiftUpRange(@Param("dayPlanId") Long dayPlanId,
      @Param("fromOrder") Integer fromOrder,
      @Param("toOrder") Integer toOrder);

  // Shift “verso il basso” (usato quando sposti GIÙ un’attività: 2 -> 5)
  // Tutte nel range [from..to] diventano -1 (3->2, 4->3, 5->4)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Activity a
         set a.orderInDay = a.orderInDay - 1
       where a.dayPlan.id = :dayPlanId
         and a.orderInDay >= :fromOrder
         and a.orderInDay <= :toOrder
      """)
  int shiftDownRange(@Param("dayPlanId") Long dayPlanId,
      @Param("fromOrder") Integer fromOrder,
      @Param("toOrder") Integer toOrder);

  // Dopo DELETE compattiamo: tutti quelli > removedOrder scendono di 1
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Activity a
         set a.orderInDay = a.orderInDay - 1
       where a.dayPlan.id = :dayPlanId
         and a.orderInDay > :removedOrder
      """)
  int shiftDownAfter(@Param("dayPlanId") Long dayPlanId,
      @Param("removedOrder") int removedOrder);

  // Max order attuale (per clamp)
  @Query("select coalesce(max(a.orderInDay), 0) from Activity a where a.dayPlan.id = :dayPlanId")
  int maxOrderInDay(@Param("dayPlanId") Long dayPlanId);

  // Conta quante attività ha il giorno
  long countByDayPlanId(Long dayPlanId);

  
}
