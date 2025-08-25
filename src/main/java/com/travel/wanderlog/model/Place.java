package com.travel.wanderlog.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "places", uniqueConstraints = @UniqueConstraint(name = "uk_places_provider_pid", columnNames = {
        "provider", "provider_place_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String provider; // "nominatim" | "google" | ...

    @Column(name = "provider_place_id", nullable = false, length = 128)
    private String providerPlaceId; 

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "formatted_address", length = 500)
    private String formattedAddress;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @Column(name = "categories_csv", columnDefinition = "TEXT")
    private String categoriesCsv; // es. "museum,art,poi"

    private Double rating; // opzionale (da Google)
    private Integer userRatingsTotal; // "
    private Integer priceLevel; // "

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}
