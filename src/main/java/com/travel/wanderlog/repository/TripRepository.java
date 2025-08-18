package com.travel.wanderlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.travel.wanderlog.model.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query("""
              select t from Trip t
              where t.owner.email = :email
              order by t.id desc
            """)
    List<Trip> findAllByOwnerEmailOrderByIdDesc(String email);
}
