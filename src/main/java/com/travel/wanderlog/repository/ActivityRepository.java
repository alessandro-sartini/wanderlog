package com.travel.wanderlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel.wanderlog.model.Activity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
  List<Activity> findByDayPlanIdOrderByOrderInDayAsc(Long dayPlanId);
  boolean existsByDayPlanIdAndOrderInDay(Long dayPlanId, Integer orderInDay);
}
