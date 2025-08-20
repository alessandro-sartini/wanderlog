package com.travel.wanderlog.dto.place;

import java.util.List;

public record PlaceDto(
        String provider, // es. "nominatim"
        String providerPlaceId, // es. "node:123456" | "way:789" | "google:ChIJ..."
        String name,
        String formattedAddress,
        Double lat,
        Double lon,
        List<String> categories, // es. ["restaurant","pizza"]

        Double rating, // opzionale (Google)
        Integer userRatingsTotal, // opzionale (Google)
        Integer priceLevel, // opzionale (Google)
        List<String> photos // opzionale (Google)
) {
}
