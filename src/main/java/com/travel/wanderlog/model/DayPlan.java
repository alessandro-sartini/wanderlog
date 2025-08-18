package com.travel.wanderlog.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "day_plans")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DayPlan {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  private LocalDate date;

  @Column(name = "index_in_trip", nullable = false)
  private Integer indexInTrip;

  // ---- nuovi campi V2 ----
  @Column(length = 255)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String note;

  @Column(name = "main_place_name", length = 255)
  private String mainPlaceName;

  @Column(name = "main_place_address", length = 500)
  private String mainPlaceAddress;

  @Column(name = "main_place_place_id", length = 128)
  private String mainPlacePlaceId;

  @Column(name = "main_place_latitude")
  private Double mainPlaceLatitude;

  @Column(name = "main_place_longitude")
  private Double mainPlaceLongitude;
}

