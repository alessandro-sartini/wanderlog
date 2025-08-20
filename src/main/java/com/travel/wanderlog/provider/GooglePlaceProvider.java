package com.travel.wanderlog.provider;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.travel.wanderlog.dto.place.PlaceDto;

@Service
@ConditionalOnProperty(name = "app.places.provider", havingValue = "google")
public class GooglePlaceProvider implements PlaceSearchProvider {

    private final RestClient http;
    private final String apiKey;

    public GooglePlaceProvider(
            @Value("${app.places.google.base-url}") String baseUrl,
            @Value("${app.places.google.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public String providerName() {
        return "google";
    }

    @Override
    public List<PlaceDto> search(String query, Double lat, Double lon, Integer limit) {
        // Esempio: /textsearch/json?query=...&key=... .
        throw new UnsupportedOperationException("TODO: implementami quando attivi Google");
    }

    @Override
    public Optional<PlaceDto> details(String providerPlaceId) {
        // /details/json?place_id=...&key=...
        throw new UnsupportedOperationException("TODO: implementami quando attivi Google");
    }

    @Override
    public Optional<PlaceDto> reverse(double lat, double lon) {
        // Google non ha un reverse diretto nei Places; puoi usare Geocoding.
        throw new UnsupportedOperationException("Non supportato in Places; usa Geocoding API");
    }
}
