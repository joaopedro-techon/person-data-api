package com.example.mockapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthCityResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("birthCity")
    private String birthCity;
    
    @JsonProperty("birthState")
    private String birthState;
    
    @JsonProperty("birthCountry")
    private String birthCountry;
    
    @JsonProperty("coordinates")
    private Coordinates coordinates;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        @JsonProperty("latitude")
        private Double latitude;
        
        @JsonProperty("longitude")
        private Double longitude;
    }
}

