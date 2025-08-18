package com.travel.wanderlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.Activity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
  List<Activity> findByDayPlanIdOrderByOrderInDayAsc(Long dayPlanId);

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

  @Modifying
  @Query("""
        UPDATE Activity a
           SET a.orderInDay = a.orderInDay + 1
         WHERE a.dayPlan.id = :dayPlanId
           AND a.orderInDay >= :fromOrder AND a.orderInDay <= :toOrder
      """)
  int shiftUpRange(@Param("dayPlanId") Long dayPlanId,
      @Param("fromOrder") Integer fromOrder,
      @Param("toOrder") Integer toOrder);

  @Modifying
  @Query("""
        UPDATE Activity a
           SET a.orderInDay = a.orderInDay - 1
         WHERE a.dayPlan.id = :dayPlanId
           AND a.orderInDay >= :fromOrder AND a.orderInDay <= :toOrder
      """)
  int shiftDownRange(@Param("dayPlanId") Long dayPlanId,
      @Param("fromOrder") Integer fromOrder,
      @Param("toOrder") Integer toOrder);

  @Query("select coalesce(max(a.orderInDay),0) from Activity a where a.dayPlan.id = :dayPlanId")
  int findMaxOrderInDay(@Param("dayPlanId") Long dayPlanId);
}
