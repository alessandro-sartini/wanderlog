package com.travel.wanderlog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.travel.wanderlog.model.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

  Optional<Place> findByProviderAndProviderPlaceId(String provider, String providerPlaceId);

  // search locale semplice su name/address; aggiungi lat/lon se vuoi filtro
  // geografico
  @Query("""
        select p from Place p
         where (:term is null or :term = ''
               or lower(p.name) like lower(concat('%',:term,'%'))
               or lower(p.formattedAddress) like lower(concat('%',:term,'%')))
         order by p.id desc
      """)
  List<Place> searchLocal(String term);

  // DB-first: cerca per testo (name/address) e in area (bounding box)
  @Query("""
        select p from Place p
        where
          ( (:text is null) or
            ( lower(p.name) like lower(concat('%', :text, '%')) or
              lower(p.formattedAddress) like lower(concat('%', :text, '%')) ) )
          and (
            (:lat is null or :lon is null)
            or ( p.lat between (:lat - :delta) and (:lat + :delta)
                 and p.lon between (:lon - :delta) and (:lon + :delta) )
          )
        order by p.id desc
      """)
  List<Place> searchByNameAndArea(
      @Param("text") String text,
      @Param("lat") Double lat,
      @Param("lon") Double lon,
      @Param("delta") Double delta,
      Pageable pageable);

  // fallback: solo match testuale
  @Query("""
        select p from Place p
        where lower(p.name) like lower(concat('%', :term, '%'))
           or lower(p.formattedAddress) like lower(concat('%', :term, '%'))
        order by p.id desc
      """)
  List<Place> searchLocal(@Param("term") String term, Pageable pageable);
}
