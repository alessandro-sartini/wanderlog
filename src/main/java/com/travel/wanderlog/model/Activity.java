package com.travel.wanderlog.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.time.Instant;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_plan_id", nullable = false)
    private DayPlan dayPlan;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalTime startTime; // ora di inizio
    private Integer durationMinutes; // durata stimata in minuti

    // Località (opzionale)
    @Column(name = "place_name", length = 255)
    private String placeName;

    @Column(name = "place_address", length = 500)
    private String placeAddress;

    @Column(name = "place_place_id", length = 128)
    private String placeId;

    @Column(name = "place_latitude")
    private Double placeLatitude;

    @Column(name = "place_longitude")
    private Double placeLongitude;

    @Column(name = "order_in_day", nullable = false)
    private Integer orderInDay; // posizione nell'elenco del giorno

    // gestiti dal DB
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(name = "activity_tags", joinColumns = @JoinColumn(name = "activity_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

}
