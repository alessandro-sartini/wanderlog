package com.travel.wanderlog.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tags")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tag {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(length = 50)
  private String type;

  @Column(length = 120)
  private String slug;
}
