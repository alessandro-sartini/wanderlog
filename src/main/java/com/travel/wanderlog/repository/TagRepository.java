package com.travel.wanderlog.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.travel.wanderlog.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
}
