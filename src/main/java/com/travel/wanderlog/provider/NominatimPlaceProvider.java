package com.travel.wanderlog.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.travel.wanderlog.dto.place.PlaceDto;

@Service
@ConditionalOnProperty(name = "app.places.provider", havingValue = "nominatim", matchIfMissing = true)
public class NominatimPlaceProvider implements PlaceSearchProvider {

  private final RestClient http;

  public NominatimPlaceProvider(
      @Value("${app.places.nominatim.base-url}") String baseUrl,
      @Value("${app.places.nominatim.user-agent}") String userAgent) {
    this.http = RestClient.builder()
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
      .build();
  }

  @Override public String providerName() { return "nominatim"; }

  @Override
  public List<PlaceDto> search(String query, Double lat, Double lon, Integer limit) {
    int lim = (limit == null ? 10 : Math.max(1, Math.min(limit, 30)));
    var items = http.get()
      .uri(b -> {
        var u = b.path("/search")
          .queryParam("format", "jsonv2")
          .queryParam("q", query)
          .queryParam("limit", lim)
          .queryParam("addressdetails", 1);
        if (lat != null && lon != null) u.queryParam("lat", lat).queryParam("lon", lon);
        return u.build();
      })
      .retrieve()
      .body(new ParameterizedTypeReference<List<NominatimItem>>() {});
    var out = new ArrayList<PlaceDto>();
    if (items != null) for (var it : items) out.add(toDto(it));
    return out;
  }

  @Override
  public Optional<PlaceDto> reverse(double lat, double lon) {
    var r = http.get()
      .uri(b -> b.path("/reverse")
        .queryParam("format", "jsonv2")
        .queryParam("lat", lat)
        .queryParam("lon", lon)
        .queryParam("addressdetails", 1)
        .build())
      .retrieve()
      .body(NominatimReverse.class);
    return (r == null) ? Optional.empty() : Optional.of(toDto(r));
  }

  @Override
  public Optional<PlaceDto> details(String providerPlaceId) {
    var parts = providerPlaceId.split(":");
    if (parts.length != 2) return Optional.empty();
    String prefix = switch (parts[0]) { case "node" -> "N"; case "way" -> "W"; case "relation" -> "R"; default -> ""; };
    if (prefix.isEmpty()) return Optional.empty();

    var list = http.get()
      .uri(b -> b.path("/lookup")
        .queryParam("format", "jsonv2")
        .queryParam("osm_ids", prefix + parts[1])
        .queryParam("addressdetails", 1)
        .build())
      .retrieve()
      .body(new ParameterizedTypeReference<List<NominatimItem>>() {});
    if (list == null || list.isEmpty()) return Optional.empty();
    return Optional.of(toDto(list.get(0)));
  }

  // ---- mapping ----
  private static PlaceDto toDto(NominatimItem it) {
    Double la = safeDouble(it.lat); Double lo = safeDouble(it.lon);
    String provId = it.osmType + ":" + it.osmId;
    var cats = new ArrayList<String>();
    if (it.category != null && !it.category.isBlank()) cats.add(it.category);
    if (it.type != null && !it.type.isBlank()) cats.add(it.type);
    return new PlaceDto("nominatim", provId,
      pickName(it.displayName, it.address),
      it.displayName, la, lo, cats,
      null, null, null, null);
  }

  private static PlaceDto toDto(NominatimReverse r) {
    Double la = safeDouble(r.lat); Double lo = safeDouble(r.lon);
    String provId = r.osmType + ":" + r.osmId;
    return new PlaceDto("nominatim", provId,
      pickName(r.displayName, r.address),
      r.displayName, la, lo, List.of(),
      null, null, null, null);
  }

  private static Double safeDouble(String s) { try { return s == null ? null : Double.parseDouble(s); } catch (Exception e) { return null; } }

  private static String pickName(String display, Map<String, Object> addr) {
    if (addr != null) {
      for (var key : new String[]{"amenity","tourism","shop","leisure"}) {
        Object v = addr.get(key);
        if (v instanceof String s && !s.isBlank()) return s;
      }
    }
    return display;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class NominatimItem {
    @JsonProperty("display_name") public String displayName;
    @JsonProperty("lat") public String lat;
    @JsonProperty("lon") public String lon;
    @JsonProperty("osm_type") public String osmType;
    @JsonProperty("osm_id") public Long osmId;
    @JsonProperty("category") public String category;
    @JsonProperty("type") public String type;
    @JsonProperty("address") public Map<String,Object> address;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class NominatimReverse {
    @JsonProperty("display_name") public String displayName;
    @JsonProperty("lat") public String lat;
    @JsonProperty("lon") public String lon;
    @JsonProperty("osm_type") public String osmType;
    @JsonProperty("osm_id") public Long osmId;
    @JsonProperty("address") public Map<String,Object> address;
  }
}
