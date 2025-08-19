package com.travel.wanderlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.DayPlan;
import com.travel.wanderlog.model.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {

  // listing
  List<Trip> findByOwnerIdOrderByOrderInOwnerAsc(Long ownerId);

  List<Trip> findByOwnerEmailOrderByOrderInOwnerAsc(String email);

  @Query("select dp from DayPlan dp where dp.trip.id = :tripId order by dp.indexInTrip asc")
  List<DayPlan> findDaysByTripIdOrderByIndex(@Param("tripId") Long tripId);

  // max ordine per owner
  @Query("select coalesce(max(t.orderInOwner), 0) from Trip t where t.owner.id = :ownerId")
  int maxOrderInOwner(@Param("ownerId") Long ownerId);

  // parking
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update Trip t set t.orderInOwner = 0 where t.id = :tripId")
  int park(@Param("tripId") Long tripId);

  // shift up range [from..to] => +1
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Trip t
         set t.orderInOwner = t.orderInOwner + 1
       where t.owner.id = :ownerId
         and t.orderInOwner >= :fromOrder
         and t.orderInOwner <= :toOrder
      """)
  int shiftUpRange(@Param("ownerId") Long ownerId,
      @Param("fromOrder") int fromOrder,
      @Param("toOrder") int toOrder);

  // shift down range [from..to] => -1
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Trip t
         set t.orderInOwner = t.orderInOwner - 1
       where t.owner.id = :ownerId
         and t.orderInOwner >= :fromOrder
         and t.orderInOwner <= :toOrder
      """)
  int shiftDownRange(@Param("ownerId") Long ownerId,
      @Param("fromOrder") int fromOrder,
      @Param("toOrder") int toOrder);

  // compatta dopo delete (se ti serve)
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("""
      update Trip t
         set t.orderInOwner = t.orderInOwner - 1
       where t.owner.id = :ownerId
         and t.orderInOwner > :removedOrder
      """)
  int shiftDownAfter(@Param("ownerId") Long ownerId,
      @Param("removedOrder") int removedOrder);




      
}
