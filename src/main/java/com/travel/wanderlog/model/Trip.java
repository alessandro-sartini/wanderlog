package com.travel.wanderlog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // PK auto-increment MySQL
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // molti Trip -> un User
  @JoinColumn(name = "owner_id", nullable = false) // FK verso users.id (coerente con V1)
  private User owner;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private LocalDate startDate;
  private LocalDate endDate;

  @Enumerated(EnumType.STRING) // salva enum come stringa leggibile
  @Column(nullable = false)
  private Visibility visibility;

  // Gestiti dal DB (DEFAULT/ON UPDATE) — coerenti con V1__init.sql
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Instant updatedAt;

  @Column(name = "order_in_owner", nullable = false)
  private Integer orderInOwner;

}